package com.example.pinjampak.data.remote.dto

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
