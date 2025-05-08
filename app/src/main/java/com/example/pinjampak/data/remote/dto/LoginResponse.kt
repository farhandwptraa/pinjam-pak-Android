package com.example.pinjampak.data.remote.dto

data class LoginResponse(
    val token: String,
    val role_id: String,
    val username: String,
    val role: String
)