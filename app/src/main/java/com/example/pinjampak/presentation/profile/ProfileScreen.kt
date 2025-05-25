package com.example.pinjampak.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.utils.Constants
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user = viewModel.userProfile
    val customer = viewModel.customerProfile
    val isLoading = viewModel.isLoading

    val gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Profil Pengguna",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    user?.let {
                        item {
                            ProfileCard {
                                ProfileRow(label = "Username", value = it.username)
                                ProfileRow(label = "Nama Lengkap", value = it.namaLengkap)
                                ProfileRow(label = "Email", value = it.email)
                            }
                        }
                    }

                    customer?.let {
                        item {
                            ProfileCard {
                                ProfileRow(label = "NIK", value = it.nik)
                                ProfileRow(label = "Tempat Lahir", value = it.tempatLahir)
                                ProfileRow(label = "Tanggal Lahir", value = formatTanggalLahir(it.tanggalLahir))
                                ProfileRow(label = "Pekerjaan", value = it.pekerjaan)
                                ProfileRow(label = "Gaji", value = formatCurrency(it.gaji))
                                ProfileRow(label = "No HP", value = it.noHp)
                                ProfileRow(label = "Nama Ibu Kandung", value = it.namaIbuKandung)
                                ProfileRow(label = "Alamat", value = it.alamat)
                                ProfileRow(label = "Provinsi", value = it.provinsi)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Constants.CHANGE_PASSWORD) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Ganti Password")
                }

                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Constants.DESTINATION_LOGIN)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
        Divider(modifier = Modifier.padding(top = 4.dp))
    }
}

fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(value)}"
}

fun formatTanggalLahir(isoDate: String): String {
    return try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val date = isoFormat.parse(isoDate)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        outputFormat.format(date!!)
    } catch (e: Exception) {
        isoDate // fallback jika parsing gagal
    }
}
