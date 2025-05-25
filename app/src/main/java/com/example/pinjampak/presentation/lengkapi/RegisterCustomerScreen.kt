package com.example.pinjampak.presentation.lengkapi

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import com.example.pinjampak.utils.Constants
import com.example.pinjampak.utils.uriToFile
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RegisterCustomerScreen(
    navController: NavController,
    viewModel: RegisterCustomerViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val state = rememberRegisterCustomerFormState(viewModel.ktpUri)
    val provinsiList by viewModel.provinsiList.collectAsStateWithLifecycle()
    val kotaList by viewModel.kotaList.collectAsStateWithLifecycle()

    val ktpLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            state.ktpUri = it
            viewModel.ktpUri = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProvinsi()
        viewModel.eventFlow.collect { event ->
            when (event) {
                is RegisterCustomerViewModel.UiEvent.Success -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshHome", true)
                    snackbarHostState.showSnackbar(event.message)
                    navController.popBackStack()
                }
                is RegisterCustomerViewModel.UiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lengkapi Data Customer",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Isi semua data dengan benar dan unggah foto KTP.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RegisterCustomerForm(
                        state = state,
                        provinsiList = provinsiList.map { it.name },
                        kotaList = kotaList,
                        isLoading = isLoading,
                        onProvinsiSelected = { name ->
                            state.selectedProvinsi = name
                            state.selectedKota = ""
                            viewModel.loadKota(provinsiList.find { it.name == name }?.id ?: 0)
                        },
                        onSubmit = {
                            if (!state.isValid()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Lengkapi semua data dan upload foto KTP")
                                }
                                return@RegisterCustomerForm
                            }

                            val file = uriToFile(state.ktpUri!!, context)
                            val request = state.toRegisterCustomerRequest()
                            viewModel.registerCustomer(request, file)
                        },
                        onPickImage = { ktpLauncher.launch("image/*") },
                        onCaptureImage = { navController.navigate(Constants.CAMERA_CAPTURE) }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterCustomerForm(
    state: RegisterCustomerFormState,
    provinsiList: List<String>,
    kotaList: List<String>,
    isLoading: Boolean,
    onProvinsiSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = state.nik, onValueChange = { state.nik = it }, label = { Text("NIK") })
            OutlinedTextField(value = state.tempatLahir, onValueChange = { state.tempatLahir = it }, label = { Text("Tempat Lahir") })
            DatePickerField(state.selectedTanggalLahir) { formatted, forRequest ->
                state.selectedTanggalLahir = formatted
                state.tanggalLahirForRequest = forRequest
            }
            OutlinedTextField(value = state.pekerjaan, onValueChange = { state.pekerjaan = it }, label = { Text("Pekerjaan") })
            OutlinedTextField(value = state.gaji, onValueChange = { state.gaji = it }, label = { Text("Gaji") })
            OutlinedTextField(value = state.noHp, onValueChange = { state.noHp = it }, label = { Text("No HP") })
            OutlinedTextField(value = state.namaIbu, onValueChange = { state.namaIbu = it }, label = { Text("Nama Ibu Kandung") })
            OutlinedTextField(value = state.alamat, onValueChange = { state.alamat = it }, label = { Text("Alamat Jalan") })

            DropdownString("Provinsi", provinsiList, state.selectedProvinsi, onProvinsiSelected)
            DropdownString("Kota/Kabupaten", kotaList, state.selectedKota) { state.selectedKota = it }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onPickImage) { Text("Pilih Foto") }
                Button(onClick = onCaptureImage) { Text("Ambil Foto") }
            }

            state.ktpUri?.let {
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth().height(150.dp))
            }

            Button(onClick = onSubmit, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                Text(if (isLoading) "Mengirim..." else "Kirim")
            }
        }
    }
}

@Composable
fun DatePickerField(selectedDate: String, onDateSelected: (String, String) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val displayFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
    val requestFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(context, { _, y, m, d ->
            calendar.set(y, m, d)
            val date = calendar.time
            onDateSelected(displayFormat.format(date), requestFormat.format(date))
            showDatePicker = false
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text("Tanggal Lahir") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
            }
        },
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
    )
}

@Composable
fun rememberRegisterCustomerFormState(initialKtpUri: Uri?): RegisterCustomerFormState {
    return remember {
        RegisterCustomerFormState().apply {
            ktpUri = initialKtpUri
        }
    }
}

class RegisterCustomerFormState {
    var nik by mutableStateOf("")
    var tempatLahir by mutableStateOf("")
    var selectedTanggalLahir by mutableStateOf("")
    var tanggalLahirForRequest by mutableStateOf("")
    var pekerjaan by mutableStateOf("")
    var gaji by mutableStateOf("")
    var noHp by mutableStateOf("")
    var namaIbu by mutableStateOf("")
    var alamat by mutableStateOf("")
    var selectedProvinsi by mutableStateOf("")
    var selectedKota by mutableStateOf("")
    var ktpUri: Uri? by mutableStateOf(null)

    fun isValid(): Boolean =
        nik.isNotBlank() && tempatLahir.isNotBlank() && selectedTanggalLahir.isNotBlank() && pekerjaan.isNotBlank() &&
                gaji.toIntOrNull() != null && noHp.isNotBlank() && namaIbu.isNotBlank() && alamat.isNotBlank() &&
                selectedProvinsi.isNotBlank() && selectedKota.isNotBlank() && ktpUri != null

    fun toRegisterCustomerRequest(): RegisterCustomerRequest =
        RegisterCustomerRequest(
            nik = nik,
            tempat_lahir = tempatLahir,
            tanggal_lahir = tanggalLahirForRequest,
            pekerjaan = pekerjaan,
            gaji = gaji.toInt(),
            no_hp = noHp,
            nama_ibu_kandung = namaIbu,
            alamat = "$alamat, $selectedKota",
            provinsi = selectedProvinsi
        )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownString(label: String, items: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}