package com.example.pinjampak

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.pinjampak.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

private fun getFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            Log.d("FCM", "FCM Token: $token")
            // Here you can save the token, send it to your server, etc.
        } else {
            Log.w("FCM", "Fetching FCM token failed", task.exception)
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        getFCMToken()
        setContent {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
        }
    }
}