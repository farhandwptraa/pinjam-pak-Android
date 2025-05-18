package com.example.pinjampak.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pinjampak.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")

            val title = remoteMessage.data["title"] ?: "PinjamPak"
            val body = remoteMessage.data["body"] ?: "Ada notifikasi baru"
            showNotification(title, body)
        }

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            // Kalau mau handle notifikasi dari remoteMessage.notification juga bisa diproses di sini
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // Kirim token baru ke backend kalau perlu
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "pinjampak_channel_id"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat notification channel untuk Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PinjamPak Notifications",
                NotificationManager.IMPORTANCE_HIGH // PENTING supaya muncul heads-up notification
            ).apply {
                description = "Channel notifikasi untuk aplikasi PinjamPak"
                enableLights(true)
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti dengan icon app kamu
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioritas tinggi supaya muncul pop up
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Suara, getar, lampu notifikasi aktif
            .build()

        notificationManager.notify(0, notification)
    }
}