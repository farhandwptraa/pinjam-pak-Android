package com.example.pinjampak.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pinjampak.data.remote.dto.PengajuanResponse
import com.example.pinjampak.data.remote.dto.PinjamanResponse

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Text("Error: ${state.error}")
        }

        else -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text("Riwayat Pengajuan", style = MaterialTheme.typography.titleMedium)
                }
                items(state.pengajuanList) { item ->
                    PengajuanItem(item)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Riwayat Pinjaman", style = MaterialTheme.typography.titleMedium)
                }
                items(state.pinjamanList) { item ->
                    PinjamanItem(item)
                }
            }
        }
    }
}

@Composable
fun PengajuanItem(item: PengajuanResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${item.idPengajuan}")
            Text("Jumlah: Rp${item.amount}")
            Text("Status: ${item.status}")
            Text("Tanggal: ${item.tanggalPengajuan}")
            if (!item.catatanMarketing.isNullOrBlank()) {
                Text("Catatan: ${item.catatanMarketing}")
            }
        }
    }
}

@Composable
fun PinjamanItem(item: PinjamanResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${item.idPinjaman}")
            Text("Jumlah: Rp${item.amount}")
            Text("Status: ${item.status}")
            Text("Pencairan: ${item.tanggalPencairan}")
            Text("Bunga: ${item.bunga}%")
        }
    }
}