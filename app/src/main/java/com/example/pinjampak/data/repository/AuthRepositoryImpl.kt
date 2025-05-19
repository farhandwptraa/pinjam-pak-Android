package com.example.pinjampak.data.repository

import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.api.AuthApi
import com.example.pinjampak.data.remote.dto.FcmTokenRequest
import com.example.pinjampak.data.remote.dto.ForgotPasswordRequest
import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginResponse
import com.example.pinjampak.data.remote.dto.LoginWithGoogleRequest
import com.example.pinjampak.data.remote.dto.RegisterRequest
import com.example.pinjampak.data.remote.dto.RegisterResponse
import com.example.pinjampak.data.remote.dto.ResetPasswordRequest
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.utils.SharedPrefManager
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val apiService: ApiService,
    private val sharedPrefManager: SharedPrefManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): LoginResponse {
        return authApi.login(request)
    }

    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = authApi.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String) {
        apiService.forgotPassword(ForgotPasswordRequest(email))
    }

    override suspend fun resetPassword(token: String, newPassword: String) {
        val request = ResetPasswordRequest(token, newPassword)
        apiService.resetPassword(request)
    }

    override suspend fun sendFcmTokenToBackend(fcmToken: String) {
        val token = sharedPrefManager.getToken()
        val username = sharedPrefManager.getUsername()

        authApi.sendFcmToken(
            token = "Bearer $token",
            request = FcmTokenRequest(username = username.toString(), fcmToken = fcmToken)
        )
    }

    override suspend fun loginWithGoogle(request: LoginWithGoogleRequest): LoginResponse {
        return authApi.loginWithGoogle(request)
    }
}
