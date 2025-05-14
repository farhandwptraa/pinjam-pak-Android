package com.example.pinjampak.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.utils.Constants

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isCustomerDataComplete by viewModel.isCustomerDataComplete.collectAsState(initial = false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Login Berhasil! Selamat datang di Home Screen.",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isCustomerDataComplete) {
                Button(
                    onClick = {
                        navController.navigate(Constants.DESTINATION_REGISTER_CUSTOMER)
                    }
                ) {
                    Text("Lengkapi Data")
                }
            }
        }
    }
}