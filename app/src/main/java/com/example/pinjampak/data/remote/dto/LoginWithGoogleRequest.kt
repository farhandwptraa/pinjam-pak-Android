package com.example.pinjampak.data.remote.dto

data class LoginWithGoogleRequest(
    val idToken: String,
    val fcmToken: String
)