package com.example.pinjampak.data.remote.dto

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)
