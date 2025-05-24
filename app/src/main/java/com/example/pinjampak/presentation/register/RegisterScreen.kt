package com.example.pinjampak.presentation.register

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.R
import com.example.pinjampak.presentation.register.RegisterFormFields
import com.example.pinjampak.presentation.register.RegisterViewModel
import com.example.pinjampak.utils.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Background gradient
    val gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))

    // Animation for form appearance
    var formVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Delay sebelum muncul
        formVisible = true
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHostState.showSnackbar("Registrasi berhasil. Mohon verifikasi email anda")
            navController.navigate(Constants.DESTINATION_LOGIN) {
                popUpTo(Constants.DESTINATION_REGISTER) { inclusive = true }
            }
        }
    }

    LaunchedEffect(state.error) {
        if (state.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.error)
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
                // Icon / ilustrasi
                Icon(
                    painter = painterResource(id = R.drawable.ic_register), // buat icon/register ilustrasi
                    contentDescription = "Register Illustration",
                    tint = Color.White,
                    modifier = Modifier.size(96.dp)
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
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Daftar",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            )

                            Spacer(Modifier.height(16.dp))

                            RegisterFormFields(
                                onSubmit = { username, password, email, namaLengkap, _ ->
                                    viewModel.register(username, password, email, namaLengkap)
                                },
                                isLoading = state.isLoading
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Sudah punya akun?")
                                TextButton(onClick = {
                                    navController.navigate(Constants.DESTINATION_LOGIN) {
                                        popUpTo(Constants.DESTINATION_REGISTER) { inclusive = true }
                                    }
                                }) {
                                    Text("Login")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
