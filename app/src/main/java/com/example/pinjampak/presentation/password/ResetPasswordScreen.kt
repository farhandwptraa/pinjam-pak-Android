package com.example.pinjampak.presentation.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ResetPasswordScreen(
    token: String,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    navController: NavController
) {
    var newPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var formVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        formVisible = true
    }

    LaunchedEffect(isSuccess, error) {
        if (isSuccess) {
            snackbarHostState.showSnackbar("Password berhasil direset.")
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        } else if (error != null) {
            snackbarHostState.showSnackbar(error ?: "Terjadi kesalahan.")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = formVisible,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(600)
                    ),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Password Baru") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch {
                                        viewModel.resetPassword(
                                            token,
                                            newPassword,
                                            onSuccess = {
                                                isLoading = false
                                                isSuccess = true
                                            },
                                            onError = {
                                                isLoading = false
                                                error = it
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading && newPassword.isNotBlank()
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Reset Password")
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Kembali")
                            }
                        }
                    }
                }
            }
        }
    }
}
