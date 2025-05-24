package com.example.pinjampak.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.utils.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val isDataComplete by viewModel.isCustomerDataComplete.collectAsState()
    val plafonMax by viewModel.plafonMax.collectAsState()
    val plafonSisa by viewModel.plafonSisa.collectAsState()

    var jumlahPinjaman by remember { mutableStateOf(viewModel.jumlahPinjaman) }
    var tenor by remember { mutableStateOf(viewModel.tenor) }

    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val submitResult by remember { derivedStateOf { viewModel.submitResult } }

    val gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))
    var formVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        formVisible = true
    }

    // Simulasi perhitungan total bayar dan cicilan
    val (totalBayar, cicilan) = remember(jumlahPinjaman, tenor) {
        viewModel.calculateSimulasi(jumlahPinjaman.toDoubleOrNull() ?: 0.0, tenor)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                it?.let { viewModel.setLokasiDariGPS(context, it) }
                viewModel.submitPengajuan()
            }.addOnFailureListener {
                viewModel.submitPengajuan()
            }
        } else {
            viewModel.submitPengajuan()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            PlafonCard(plafonMax, plafonSisa)

            AnimatedVisibility(
                visible = formVisible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600), { it / 2 }),
                exit = fadeOut()
            ) {
                AjukanPinjamanCard(
                    jumlahPinjaman = jumlahPinjaman,
                    onJumlahPinjamanChange = {
                        jumlahPinjaman = it
                        viewModel.jumlahPinjaman = it
                    },
                    tenor = tenor,
                    onTenorChange = {
                        tenor = it
                        viewModel.tenor = it
                    },
                    totalBayar = totalBayar.toInt(),
                    cicilan = cicilan.toInt(),
                    isDataComplete = isDataComplete,
                    isLoading = isLoading,
                    onSubmit = {
                        val permission = Manifest.permission.ACCESS_FINE_LOCATION
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getCurrentLocation(
                                com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                                CancellationTokenSource().token
                            ).addOnSuccessListener {
                                if (it != null) viewModel.setLokasiDariGPS(context, it)
                                viewModel.submitPengajuan()
                            }.addOnFailureListener {
                                viewModel.submitPengajuan()
                            }
                        } else {
                            locationPermissionLauncher.launch(permission)
                        }
                    },
                    onLengkapiData = {
                        navController.navigate(Constants.DESTINATION_REGISTER_CUSTOMER)
                    },
                    submitResult = submitResult
                )
            }
        }
    }
}

@Composable
fun PlafonCard(plafonMax: Double, plafonSisa: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Plafon",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                "Rp ${plafonMax.formatRupiah()}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Sisa Plafon",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                "Rp ${plafonSisa.formatRupiah()}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0288D1)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjukanPinjamanCard(
    jumlahPinjaman: String,
    onJumlahPinjamanChange: (String) -> Unit,
    tenor: Int,
    onTenorChange: (Int) -> Unit,
    totalBayar: Int,
    cicilan: Int,
    isDataComplete: Boolean,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onLengkapiData: () -> Unit,
    submitResult: String?,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp),
        colors = cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Ajukan Pinjaman",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = jumlahPinjaman,
                onValueChange = onJumlahPinjamanChange,
                label = { Text("Jumlah Pinjaman") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))

            DropdownMenuTenor(
                selected = tenor,
                onSelect = onTenorChange,
                options = viewModel.availableTenors
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Total Bayar: Rp ${totalBayar.formatRupiah()}")
            Text("Cicilan per bulan: Rp ${cicilan.formatRupiah()}")

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = if (!isDataComplete) onLengkapiData else onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(
                    if (!isDataComplete) "Lengkapi Data"
                    else if (isLoading) "Mengajukan..." else "Ajukan Pinjaman"
                )
            }

            Spacer(Modifier.height(8.dp))

            submitResult?.let {
                Text(
                    text = it,
                    color = if (it.contains("berhasil", true))
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuTenor(
    selected: Int,
    onSelect: (Int) -> Unit,
    options: List<Int>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text("$selected bulan")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text("$option bulan") },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun Int.formatRupiah(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return formatter.format(this)
}

fun Double.formatRupiah(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return formatter.format(this.toLong())
}