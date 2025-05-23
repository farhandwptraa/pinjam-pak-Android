package com.example.pinjampak.data.remote.api

import com.example.pinjampak.data.remote.dto.FcmTokenRequest
import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginResponse
import com.example.pinjampak.data.remote.dto.LoginWithGoogleRequest
import com.example.pinjampak.data.remote.dto.RegisterRequest
import com.example.pinjampak.data.remote.dto.RegisterResponse
import com.example.pinjampak.data.remote.dto.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApi {
    @POST("api/auth/login") // Ganti dengan endpoint login yang benar
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/fcm/token")
    suspend fun sendFcmToken(
        @Header("Authorization") token: String,
        @Body request: FcmTokenRequest
    )

    @POST("api/auth/login-google")
    suspend fun loginWithGoogle(
        @Body request: LoginWithGoogleRequest
    ): LoginResponse

    @GET("api/users/verify")
    suspend fun verifyEmail(@Query("token") token: String): Response<MessageResponse>
}
