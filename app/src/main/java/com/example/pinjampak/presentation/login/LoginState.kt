package com.example.pinjampak.presentation.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val error: String = "",
    val token: String? = null,
    val roleId: String? = null,
    val role: String? = null,
    val customerId: String? = null
)
