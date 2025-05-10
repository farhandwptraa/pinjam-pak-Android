package com.example.pinjampak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val email: String,
    val namaLengkap: String,
    val role: String
)
