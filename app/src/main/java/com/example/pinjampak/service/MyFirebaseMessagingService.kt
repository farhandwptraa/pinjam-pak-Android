package com.example.pinjampak.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM","Message data payload: " + remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d("FCM","Message Notification Body: " + remoteMessage.notification?.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM","Refreshed token: " + token)
    }
}