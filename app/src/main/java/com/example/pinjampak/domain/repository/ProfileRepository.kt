package com.example.pinjampak.domain.repository

import com.example.pinjampak.data.local.entity.CustomerProfileEntity
import com.example.pinjampak.data.local.entity.UserProfileEntity
import com.example.pinjampak.data.remote.dto.ProvinsiResponse

interface ProfileRepository {
    suspend fun fetchAndCacheUserProfile(): UserProfileEntity?
    suspend fun fetchAndCacheCustomerProfile(): CustomerProfileEntity?
    suspend fun getCachedUserProfile(username: String): UserProfileEntity?
    suspend fun getCachedCustomerProfile(username: String): CustomerProfileEntity?
    suspend fun changePassword(oldPassword: String, newPassword: String)
    suspend fun ajukanPinjaman(amount: Int, tenor: Int, lokasi: String): Boolean
    suspend fun logout(token: String, fcmToken: String?)
    suspend fun getAllProvinces(): List<ProvinsiResponse>
    suspend fun getCitiesByProvince(provinceId: Long): List<String>
}
