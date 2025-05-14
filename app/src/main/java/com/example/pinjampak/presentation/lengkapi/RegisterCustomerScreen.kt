package com.example.pinjampak.presentation.lengkapi

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import com.example.pinjampak.utils.Constants
import com.example.pinjampak.utils.uriToFile
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun RegisterCustomerScreen(
    navController: NavController,
    viewModel: RegisterCustomerViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Form state
    var nik by remember { mutableStateOf("") }
    var tempatLahir by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var pekerjaan by remember { mutableStateOf("") }
    var gaji by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var namaIbu by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var provinsi by remember { mutableStateOf("") }
    var ktpUri by remember { mutableStateOf<Uri?>(viewModel.ktpUri) }

    val ktpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            ktpUri = it
            viewModel.ktpUri = it
        }
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is RegisterCustomerViewModel.UiEvent.ShowError ->
                    snackbarHostState.showSnackbar(event.message)

                is RegisterCustomerViewModel.UiEvent.Success -> {
                    snackbarHostState.showSnackbar(event.message)
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = nik, onValueChange = { nik = it }, label = { Text("NIK") })
            OutlinedTextField(value = tempatLahir, onValueChange = { tempatLahir = it }, label = { Text("Tempat Lahir") })
            OutlinedTextField(value = tanggalLahir, onValueChange = { tanggalLahir = it }, label = { Text("Tanggal Lahir (YYYY-MM-DD)") })
            OutlinedTextField(value = pekerjaan, onValueChange = { pekerjaan = it }, label = { Text("Pekerjaan") })
            OutlinedTextField(value = gaji, onValueChange = { gaji = it }, label = { Text("Gaji") })
            OutlinedTextField(value = noHp, onValueChange = { noHp = it }, label = { Text("No HP") })
            OutlinedTextField(value = namaIbu, onValueChange = { namaIbu = it }, label = { Text("Nama Ibu Kandung") })
            OutlinedTextField(value = alamat, onValueChange = { alamat = it }, label = { Text("Alamat") })
            OutlinedTextField(value = provinsi, onValueChange = { provinsi = it }, label = { Text("Provinsi") })

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { ktpLauncher.launch("image/*") }) {
                    Text("Pilih Foto")
                }
                Button(onClick = {
                    navController.navigate(Constants.CAMERA_CAPTURE)
                }) {
                    Text("Ambil Foto")
                }
            }

            ktpUri?.let { uri ->
                AsyncImage(model = uri, contentDescription = null, modifier = Modifier.height(150.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nik.isBlank() || ktpUri == null || gaji.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Lengkapi semua data dan upload foto KTP")
                        }
                        return@Button
                    }

                    val gajiInt = gaji.toIntOrNull()
                    if (gajiInt == null) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Gaji harus berupa angka")
                        }
                        return@Button
                    }

                    val file = uriToFile(ktpUri!!, context)
                    val request = RegisterCustomerRequest(
                        nik = nik,
                        tempat_lahir = tempatLahir,
                        tanggal_lahir = tanggalLahir,
                        pekerjaan = pekerjaan,
                        gaji = gajiInt,
                        no_hp = noHp,
                        nama_ibu_kandung = namaIbu,
                        alamat = alamat,
                        provinsi = provinsi
                    )

                    viewModel.registerCustomer(request, file)
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Mengirim..." else "Kirim")
            }
        }
    }
}
