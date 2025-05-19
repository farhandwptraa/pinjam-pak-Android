package com.example.pinjampak.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pinjampak.R
import com.example.pinjampak.utils.Constants

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    // Launcher untuk Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        viewModel.onGoogleSignInResult(result)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            // Username Field
            Text(text = "Username or Email", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = state.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Text(text = "Password", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
                IconButton(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPassword) "Hide Password" else "Show Password"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In Button
            Button(
                onClick = { launcher.launch(viewModel.getGoogleSignInIntent()) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Login dengan Google", color = Color.Black)
            }

            // Error Message
            if (state.error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = state.error, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register Navigation
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Belum punya akun?")
                TextButton(onClick = { navController.navigate(Constants.DESTINATION_REGISTER) }) {
                    Text("Daftar")
                }
            }

            // Forgot Password Navigation
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lupa kata sandi? ")
                TextButton(onClick = { navController.navigate("forgot_password") }) {
                    Text("Klik di sini")
                }
            }

            // Auto Navigate if login success
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