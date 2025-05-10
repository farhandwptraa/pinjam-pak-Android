package com.example.pinjampak.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("role") val role: String
)
