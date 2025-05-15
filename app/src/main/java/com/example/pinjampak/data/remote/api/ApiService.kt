package com.example.pinjampak.data.remote.api

import com.example.pinjampak.data.remote.dto.ChangePasswordRequest
import com.example.pinjampak.data.remote.dto.CustomerResponse
import com.example.pinjampak.data.remote.dto.ForgotPasswordRequest
import com.example.pinjampak.data.remote.dto.ResetPasswordRequest
import com.example.pinjampak.data.remote.dto.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("/api/users/{username}")
    suspend fun getUserByUsername(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    ): UserResponse

    @GET("/api/customers/me")
    suspend fun getCustomerMe(@Header("Authorization") token: String): CustomerResponse

    @Multipart
    @POST("/api/customers/register")
    suspend fun registerCustomer(
        @Header("Authorization") token: String,
        @Part("data") data: RequestBody,
        @Part fotoKtp: MultipartBody.Part
    ): Response<Unit> // Ubah ke Unit (tanpa body)

    @PUT("api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body body: ChangePasswordRequest
    )

    // Forgot Password
    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    )

    // Reset Password
    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    )
}