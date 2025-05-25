package com.example.pinjampak.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pinjampak.presentation.profile.formatTanggalLahir
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))
    val filteredPengajuan = state.pengajuanList.filter { it.status.lowercase() != "disbursed" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(24.dp)
    ) {
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            state.error != null -> {
                Text("Error: ${state.error}", color = Color.White)
            }

            filteredPengajuan.isEmpty() && state.pinjamanList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Belum memiliki riwayat pengajuan atau pinjaman.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (filteredPengajuan.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                "Riwayat Pengajuan",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        items(filteredPengajuan) { item ->
                            HistoryCard {
                                HistoryRow("Jumlah", formatCurrency(item.amount))
                                HistoryRow("Status", item.status)
                                HistoryRow("Tanggal", formatTanggal(item.tanggalPengajuan))
                                if (!item.catatanMarketing.isNullOrBlank()) {
                                    HistoryRow("Catatan", item.catatanMarketing)
                                }
                            }
                        }
                    }

                    if (state.pinjamanList.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Riwayat Pinjaman",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        items(state.pinjamanList) { item ->
                            HistoryCard {
                                HistoryRow("Jumlah", formatCurrency(item.amount))
                                HistoryRow("Status", item.status)
                                HistoryRow("Pencairan", formatTanggal(item.tanggalPencairan))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun HistoryRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

fun formatCurrency(value: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(value)}"
}

fun formatTanggal(isoDate: String): String {
    return try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val date = isoFormat.parse(isoDate)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        outputFormat.format(date!!)
    } catch (e: Exception) {
        isoDate // fallback jika parsing gagal
    }
}