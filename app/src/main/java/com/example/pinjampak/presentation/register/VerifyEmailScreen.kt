package com.example.pinjampak.presentation.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun VerifyEmailScreen(
    token: String,
    viewModel: VerifyEmailViewModel = hiltViewModel(),
    navController: NavHostController // ⬅️ Tambahkan parameter ini
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.verifyEmail(token)
    }

    // Redirect ke login saat sukses
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            // Delay sebentar untuk menampilkan pesan
            kotlinx.coroutines.delay(1500)
            navController.navigate("login") {
                popUpTo(0) // Clear all previous backstack
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.successMessage != null -> Text(uiState.successMessage!!, color = Color.Green)
            uiState.errorMessage != null -> Text(uiState.errorMessage!!, color = Color.Red)
        }
    }
}
