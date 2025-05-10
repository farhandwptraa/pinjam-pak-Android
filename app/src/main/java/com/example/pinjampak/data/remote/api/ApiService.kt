package com.example.pinjampak.data.remote.api

import com.example.pinjampak.data.remote.dto.UserResponse
import com.example.pinjampak.data.remote.dto.CustomerResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {
    @GET("/api/users/{username}")
    suspend fun getUserByUsername(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    ): UserResponse


    @GET("/api/customers/me")
    suspend fun getCustomerMe(@Header("Authorization") token: String): CustomerResponse
}
