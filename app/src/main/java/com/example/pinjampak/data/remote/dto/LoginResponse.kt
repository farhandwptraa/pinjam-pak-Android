package com.example.pinjampak.data.remote.dto

data class LoginResponse(
    val token: String,
    val roleId: String,
    val username: String,
    val role: String,
    val customerId: String,
    val emailVerified: Boolean
)