package com.example.pinjampak.data.remote.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val nama_lengkap: String
)
