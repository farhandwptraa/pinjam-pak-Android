package com.example.pinjampak.data.repository

import android.util.Log
import com.example.pinjampak.data.local.dao.ProfileDao
import com.example.pinjampak.data.local.entity.CustomerProfileEntity
import com.example.pinjampak.data.local.entity.UserProfileEntity
import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.dto.ChangePasswordRequest
import com.example.pinjampak.data.remote.dto.LogoutRequest
import com.example.pinjampak.data.remote.dto.PengajuanRequest
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.SharedPrefManager
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val profileDao: ProfileDao,
    private val sharedPrefManager: SharedPrefManager
) : ProfileRepository {

    override suspend fun fetchAndCacheUserProfile(): UserProfileEntity? {
        val username = sharedPrefManager.getUsername() ?: return null
        val token = sharedPrefManager.getToken() ?: return null
        val bearerToken = "Bearer $token"

        val response = apiService.getUserByUsername(bearerToken, username)
        val entity = UserProfileEntity(
            userId = response.userId,
            username = response.username,
            email = response.email,
            namaLengkap = response.namaLengkap,
            role = response.role
        )
        profileDao.insertUserProfile(entity)
        return entity
    }

    override suspend fun fetchAndCacheCustomerProfile(): CustomerProfileEntity? {
        val token = sharedPrefManager.getToken() ?: return null
        val bearerToken = "Bearer $token"
        Log.d("ProfileRepository", "Fetching customer profile with token: $bearerToken")

        return try {
            val response = apiService.getCustomerMe(bearerToken)
            Log.d("ProfileRepository", "Customer response: $response")

            val entity = CustomerProfileEntity(
                username = response.username,
                email = response.email,
                namaLengkap = response.namaLengkap,
                nik = response.nik,
                tempatLahir = response.tempatLahir,
                tanggalLahir = response.tanggalLahir,
                pekerjaan = response.pekerjaan,
                gaji = response.gaji,
                plafond = response.plafond,
                sisaPlafond = response.sisaPlafond,
                noHp = response.noHp,
                namaIbuKandung = response.namaIbuKandung,
                alamat = response.alamat,
                provinsi = response.provinsi,
                namaCabang = response.namaCabang,
                areaCabang = response.areaCabang
            )
            profileDao.insertCustomerProfile(entity)
            entity
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching customer profile", e)
            null
        }
    }

    override suspend fun getCachedUserProfile(username: String): UserProfileEntity? {
        return profileDao.getUserProfile(username)
    }

    override suspend fun getCachedCustomerProfile(username: String): CustomerProfileEntity? {
        return profileDao.getCustomerProfile(username)
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String) {
        val token = sharedPrefManager.getToken()
        apiService.changePassword(
            token = "Bearer $token",
            body = ChangePasswordRequest(oldPassword, newPassword)
        )
    }

    override suspend fun ajukanPinjaman(amount: Int, tenor: Int, lokasi: String): Boolean {
        val token = sharedPrefManager.getToken() ?: return false
        val response = apiService.ajukanPinjaman(
            PengajuanRequest(amount, tenor, lokasi),
            "Bearer $token"
        )
        return response.isSuccessful
    }

    override suspend fun logout(token: String, fcmToken: String?) {
        try {
            Log.d("ProfileRepository", "Mengirim logout ke server...")
            Log.d("ProfileRepository", "Token: $token")
            Log.d("ProfileRepository", "FCM Token: $fcmToken")

            val request = LogoutRequest(fcmToken = fcmToken)
            val authHeader = "Bearer $token"

            val response = apiService.logout(authHeader, request)

            if (response.isSuccessful) {
                Log.d("ProfileRepository", "Logout berhasil di server.")
            } else {
                Log.e("ProfileRepository", "Logout gagal: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Exception saat logout: ${e.message}")
        }
    }
}
