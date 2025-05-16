package com.example.pinjampak.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.utils.Constants

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isCustomerDataComplete by viewModel.isCustomerDataComplete.collectAsState(initial = false)

    val refreshState = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refreshHome", false)
        ?.collectAsState()

    LaunchedEffect(refreshState?.value) {
        if (refreshState?.value == true) {
            viewModel.updateCustomerDataStatus()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("refreshHome", false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!isCustomerDataComplete) {
                Button(
                    onClick = {
                        navController.navigate(Constants.DESTINATION_REGISTER_CUSTOMER)
                    }
                ) {
                    Text("Lengkapi Data")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Plafon Maksimal: Rp ${viewModel.plafonMax.toInt()}")
                    Text("Sisa Plafon: Rp ${viewModel.plafonSisa.toInt()}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.jumlahPinjaman,
                onValueChange = { viewModel.jumlahPinjaman = it },
                label = { Text("Jumlah Pinjaman") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tenor: ", modifier = Modifier.weight(1f))
                DropdownMenuTenor(viewModel.tenor) {
                    viewModel.tenor = it
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val (totalBayar, cicilan) = viewModel.getSimulasi()

            Text("Total Bayar: Rp ${totalBayar.toInt()}")
            Text("Cicilan per bulan: Rp ${cicilan.toInt()}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Implementasi pengajuan */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajukan Pinjaman")
            }
        }
    }
}

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
            listOf(3, 6, 12, 24).forEach {
                DropdownMenuItem(
                    text = { Text("$it bulan") },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
