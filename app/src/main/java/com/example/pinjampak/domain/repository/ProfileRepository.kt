package com.example.pinjampak.domain.repository

import com.example.pinjampak.data.local.entity.CustomerProfileEntity
import com.example.pinjampak.data.local.entity.UserProfileEntity

interface ProfileRepository {
    suspend fun fetchAndCacheUserProfile(): UserProfileEntity?
    suspend fun fetchAndCacheCustomerProfile(): CustomerProfileEntity?
    suspend fun getCachedUserProfile(username: String): UserProfileEntity?
    suspend fun getCachedCustomerProfile(username: String): CustomerProfileEntity?
    suspend fun changePassword(oldPassword: String, newPassword: String)
    suspend fun ajukanPinjaman(amount: Int, tenor: Int): Boolean
}
