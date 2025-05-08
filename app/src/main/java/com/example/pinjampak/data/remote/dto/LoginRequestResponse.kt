package com.example.pinjampak.data.remote.dto

data class LoginRequestResponse(
    val username: String = "",
    val password: String = "",
    val token: String? = null,
    val message: String? = null
)
