package com.example.pinjampak.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.pinjampak.presentation.history.HistoryScreen
import com.example.pinjampak.presentation.profile.ProfileScreen

@Composable
fun HomeScreen(navController: NavController) {
    val bottomNavController = rememberNavController()
    val items = listOf("history", "home", "profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    "home" -> Icons.Default.Home
                                    "profile" -> Icons.Default.Person
                                    else -> Icons.Default.History // butuh import Icons.Default.History
                                },
                                contentDescription = null
                            )
                        },
                        label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                        selected = bottomNavController.currentBackStackEntry?.destination?.route == screen,
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
            composable("home") {
                HomeContent(navController = navController) // cukup ini
            }
            composable("profile") { ProfileScreen(navController)
            }
            composable("history") {
                HistoryScreen() // ✅ Tambahkan ini
            }
        }
    }
}