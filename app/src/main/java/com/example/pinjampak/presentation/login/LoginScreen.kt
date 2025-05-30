package com.example.pinjampak.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.R
import com.example.pinjampak.utils.Constants
import kotlinx.coroutines.delay
import com.example.pinjampak.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        viewModel.onGoogleSignInResult(result)
    }

    // Animation states
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showFields by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var showBottomLinks by remember { mutableStateOf(false) }

    // Launch staggered animation on composition
    LaunchedEffect(Unit) {
        delay(100)
        showLogo = true
        delay(150)
        showTitle = true
        delay(150)
        showFields = true
        delay(150)
        showButtons = true
        delay(150)
        showError = true
        delay(150)
        showBottomLinks = true
    }

    // Background gradient
    val gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {

            AnimatedVisibility(
                visible = showLogo,
                enter = slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(durationMillis = 600)
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )
            }

            AnimatedVisibility(
                visible = showTitle,
                enter = slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                Text(
                    text = "Selamat Datang",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnPrimary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            AnimatedVisibility(
                visible = showFields,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Input fields
                        OutlinedTextField(
                            value = state.username,
                            onValueChange = { viewModel.onUsernameChange(it) },
                            label = { Text("Username atau Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(imageVector = image, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Buttons
                        Column {
                            Button(
                                onClick = { viewModel.login() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                enabled = !state.isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary
                                )
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Login")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { launcher.launch(viewModel.getGoogleSignInIntent()) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Surface
                                )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google_foreground),
                                    contentDescription = "Google Logo",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Masuk dengan Google")
                            }
                        }

                        // Error message
                        AnimatedVisibility(
                            visible = showError && state.error.isNotEmpty(),
                            enter = fadeIn(animationSpec = tween(600)),
                            exit = fadeOut(animationSpec = tween(300))
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.error,
                                color = Error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Bottom links
                        AnimatedVisibility(
                            visible = showBottomLinks,
                            enter = slideInVertically(
                                initialOffsetY = { 40 },
                                animationSpec = tween(600)
                            ) + fadeIn(animationSpec = tween(600)),
                            exit = fadeOut()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Belum punya akun?")
                                    TextButton(
                                        onClick = { navController.navigate(Constants.DESTINATION_REGISTER) },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Daftar")
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Lupa kata sandi?")
                                    TextButton(
                                        onClick = { navController.navigate("forgot_password") },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text("Klik di sini")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Auto navigate
            LaunchedEffect(state.isLoggedIn) {
                if (state.isLoggedIn) {
                    navController.navigate(Constants.DESTINATION_HOME) {
                        popUpTo(Constants.DESTINATION_LOGIN) { inclusive = true }
                    }
                }
            }
        }
    }
}