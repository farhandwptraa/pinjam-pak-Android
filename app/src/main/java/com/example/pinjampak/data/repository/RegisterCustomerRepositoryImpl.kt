package com.example.pinjampak.data.repository

import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import com.example.pinjampak.domain.repository.RegisterCustomerRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RegisterCustomerRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RegisterCustomerRepository {

    override suspend fun registerCustomer(
        token: String,
        request: RegisterCustomerRequest,
        ktpImageFile: File
    ): Result<String> {
        return try {
            val gson = Gson()

            // 🔁 Convert RegisterCustomerRequest ke JSON string
            val json = gson.toJson(request)
            val dataRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

            // 📷 Convert file ke MultipartBody.Part
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), ktpImageFile)
            val ktpPart = MultipartBody.Part.createFormData("fotoKtp", ktpImageFile.name, imageRequestBody)

            // 🚀 Kirim request
            val response = apiService.registerCustomer(
                token = "Bearer $token",
                data = dataRequestBody,
                fotoKtp = ktpPart
            )

            if (response.isSuccessful) {
                Result.success(response.body() ?: "Berhasil")
            } else {
                Result.failure(Exception("Gagal: ${response.code()} - ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
