package com.example.pinjampak.data.repository

import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.api.AuthApi
import com.example.pinjampak.data.remote.dto.*
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
        val request = ForgotPasswordRequest(email)
        apiService.forgotPassword(request)
    }

    override suspend fun resetPassword(token: String, newPassword: String) {
        val request = ResetPasswordRequest(token, newPassword)
        apiService.resetPassword(request)
    }

    override suspend fun sendFcmTokenToBackend(fcmToken: String) {
        val token = sharedPrefManager.getToken()
        val username = sharedPrefManager.getUsername().orEmpty()

        val request = FcmTokenRequest(username = username, fcmToken = fcmToken)
        authApi.sendFcmToken(token = "Bearer $token", request = request)
    }

    override suspend fun loginWithGoogle(request: LoginWithGoogleRequest): LoginResponse {
        return authApi.loginWithGoogle(request)
    }

    override suspend fun verifyEmail(token: String): String {
        return authApi.verifyEmail(token).toString()
    }
}