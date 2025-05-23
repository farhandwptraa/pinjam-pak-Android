package com.example.pinjampak.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.utils.Constants
import com.example.pinjampak.utils.LoanLevel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isDataComplete by viewModel.isCustomerDataComplete.collectAsState()
    val plafonMax by viewModel.plafonMax.collectAsState()
    val plafonSisa by viewModel.plafonSisa.collectAsState()
    var jumlahPinjaman by remember { mutableStateOf(viewModel.jumlahPinjaman) }
    var tenor by remember { mutableStateOf(viewModel.tenor) }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val submitResult by remember { derivedStateOf { viewModel.submitResult } }

    val refreshFlow = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refreshHome", false)
        ?.collectAsState()
    LaunchedEffect(refreshFlow?.value) {
        if (refreshFlow?.value == true) {
            viewModel.updateCustomerDataStatus()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("refreshHome", false)
        }
    }

    val (totalBayar, cicilan) = remember(jumlahPinjaman, tenor) {
        viewModel.calculateSimulasi(jumlahPinjaman.toDoubleOrNull() ?: 0.0, tenor)
    }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.setLokasiDariGPS(context, it)
                    }
                    viewModel.submitPengajuan()
                }.addOnFailureListener {
                    viewModel.submitPengajuan()
                }
            } else {
                viewModel.submitPengajuan()
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Home") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!isDataComplete) {
                    Button(onClick = { navController.navigate(Constants.DESTINATION_REGISTER_CUSTOMER) }) {
                        Text("Lengkapi Data")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Plafon Maksimal: Rp ${plafonMax.toInt()}")
                        Text("Sisa Plafon: Rp ${plafonSisa.toInt()}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = jumlahPinjaman,
                    onValueChange = { new ->
                        jumlahPinjaman = new
                        viewModel.jumlahPinjaman = new
                    },
                    label = { Text("Jumlah Pinjaman") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tenor:", modifier = Modifier.weight(1f))
                    DropdownMenuTenor(selected = tenor) { newTenor ->
                        tenor = newTenor
                        viewModel.tenor = newTenor
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Total Bayar: Rp ${totalBayar.toInt()}")
                Text("Cicilan per bulan: Rp ${cicilan.toInt()}")

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Loan Levels",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(LoanLevel.values().toList()) { level ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(level.description(), style = MaterialTheme.typography.bodyMedium)
                            Divider()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val permission = Manifest.permission.ACCESS_FINE_LOCATION
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            val cancellationTokenSource = CancellationTokenSource()
                            fusedLocationClient.getCurrentLocation(
                                com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                                cancellationTokenSource.token
                            ).addOnSuccessListener { location ->
                                if (location != null) {
                                    Log.d("HomeContent", "Lokasi didapat: lat=${location.latitude}, lng=${location.longitude}")
                                    viewModel.setLokasiDariGPS(context, location)
                                } else {
                                    Log.e("HomeContent", "Lokasi null saat pengambilan")
                                }
                                Log.d("HomeContent", "Mengirim pengajuan dengan lokasi: ${viewModel.lokasi}")
                                viewModel.submitPengajuan()

                            }.addOnFailureListener { e ->
                                Log.e("HomeContent", "Gagal ambil lokasi: ${e.message}")
                                Log.d("HomeContent", "Tetap melanjutkan pengajuan meskipun lokasi gagal didapat")
                                viewModel.submitPengajuan()
                            }
                        } else {
                            locationPermissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Mengajukan..." else "Ajukan Pinjaman")
                }

                submitResult?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = msg,
                        color = if (msg.contains("berhasil", ignoreCase = true)) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuTenor(selected: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text("$selected bulan")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf(3, 6, 12, 24).forEach { option ->
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
