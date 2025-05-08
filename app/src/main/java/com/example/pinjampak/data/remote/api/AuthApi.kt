package com.example.pinjampak.data.remote.api

import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login") // Ganti dengan endpoint login yang benar
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
