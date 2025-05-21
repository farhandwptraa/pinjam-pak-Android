package com.example.pinjampak.domain.repository

import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginResponse
import com.example.pinjampak.data.remote.dto.LoginWithGoogleRequest
import com.example.pinjampak.data.remote.dto.RegisterRequest
import com.example.pinjampak.data.remote.dto.RegisterResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun forgotPassword(email: String)
    suspend fun resetPassword(token: String, newPassword: String)
    suspend fun sendFcmTokenToBackend(fcmToken: String)
    suspend fun loginWithGoogle(request: LoginWithGoogleRequest): LoginResponse
    suspend fun verifyEmail(token: String): String
}
