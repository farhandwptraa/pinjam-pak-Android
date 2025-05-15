package com.example.pinjampak.data.repository

import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import com.example.pinjampak.domain.repository.RegisterCustomerRepository
import com.example.pinjampak.utils.SharedPrefManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RegisterCustomerRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefManager: SharedPrefManager
) : RegisterCustomerRepository {

    private val gson = Gson()

    override suspend fun registerCustomer(
        token: String,
        request: RegisterCustomerRequest,
        ktpImageFile: File
    ): Result<String> {
        return try {
            // Konversi file KTP ke MultipartBody.Part
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), ktpImageFile)
            val ktpPart = MultipartBody.Part.createFormData("fotoKtp", ktpImageFile.name, imageRequestBody)

            // Konversi data JSON ke RequestBody
            val dataJson = gson.toJson(request)
            val dataPart = RequestBody.create("application/json".toMediaTypeOrNull(), dataJson)

            // Panggil API
            val response = apiService.registerCustomer(
                token = "Bearer $token",
                data = dataPart,
                fotoKtp = ktpPart
            )

            if (response.isSuccessful) {
                Result.success("Customer berhasil didaftarkan")
            } else {
                Result.failure(Exception("Gagal: ${response.code()} - ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
