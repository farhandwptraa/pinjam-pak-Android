package com.example.pinjampak.data.remote.dto

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
