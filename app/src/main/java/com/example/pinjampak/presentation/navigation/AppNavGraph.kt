package com.example.pinjampak.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.pinjampak.presentation.camera.CameraCaptureScreen
import com.example.pinjampak.presentation.login.LoginScreen
import com.example.pinjampak.presentation.home.HomeScreen
import com.example.pinjampak.presentation.register.RegisterScreen
import com.example.pinjampak.presentation.lengkapi.RegisterCustomerScreen
import com.example.pinjampak.presentation.lengkapi.RegisterCustomerViewModel
import com.example.pinjampak.presentation.password.ChangePasswordScreen
import com.example.pinjampak.presentation.password.ForgotPasswordScreen
import com.example.pinjampak.presentation.password.ResetPasswordScreen
import com.example.pinjampak.utils.Constants

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Constants.DESTINATION_LOGIN
    ) {
        composable(Constants.DESTINATION_LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Constants.DESTINATION_HOME) {
            HomeScreen(navController = navController)
        }
        composable(Constants.DESTINATION_REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Constants.DESTINATION_REGISTER_CUSTOMER) {
            val viewModel: RegisterCustomerViewModel = hiltViewModel()
            RegisterCustomerScreen(navController = navController, viewModel = viewModel)
        }
        composable(Constants.CAMERA_CAPTURE) {
            CameraCaptureScreen(navController = navController)
        }
        composable(Constants.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }
        composable(Constants.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }

        // Deep Link untuk reset password
        composable(
            route = "reset_password/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "pinjampak://reset-password?token={token}"
                }
            )
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ResetPasswordScreen(token = token, navController = navController)
        }
    }
}
