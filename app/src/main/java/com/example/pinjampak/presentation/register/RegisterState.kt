package com.example.pinjampak.presentation.register

data class RegisterState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String = ""
)