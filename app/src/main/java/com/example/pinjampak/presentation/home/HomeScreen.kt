// HomeScreen.kt
package com.example.pinjampak.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.pinjampak.presentation.profile.ProfileScreen
import com.example.pinjampak.presentation.home.components.HomeContent

@Composable
fun HomeScreen(navController: NavController) {
    val bottomNavController = rememberNavController()
    val items = listOf("home", "profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (screen == "home") Icons.Default.Home else Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                        selected = bottomNavController.currentDestination?.route == screen,
                        onClick = {
                            bottomNavController.navigate(screen) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeContent() }
            composable("profile") { ProfileScreen(navController) } // ← gunakan NavController utama di sini
        }
    }
}