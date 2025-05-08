package com.example.pinjampak.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pinjampak.presentation.login.LoginScreen
import com.example.pinjampak.presentation.home.HomeScreen
import com.example.pinjampak.utils.Constants
import com.example.pinjampak.presentation.register.RegisterScreen

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
            HomeScreen()
        }
        composable(Constants.DESTINATION_REGISTER) {
            RegisterScreen(navController = navController)
        }
    }
}
