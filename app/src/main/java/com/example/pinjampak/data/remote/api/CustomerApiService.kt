package com.example.pinjampak.data.remote.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CustomerApiService {

    @Multipart
    @POST("/api/customers/register")
    suspend fun registerCustomer(
        @Header("Authorization") token: String,
        @Part("nik") nik: RequestBody,
        @Part("tempat_lahir") tempatLahir: RequestBody,
        @Part("tanggal_lahir") tanggalLahir: RequestBody,
        @Part("pekerjaan") pekerjaan: RequestBody,
        @Part("gaji") gaji: RequestBody,
        @Part("no_hp") noHp: RequestBody,
        @Part("nama_ibu_kandung") namaIbuKandung: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("provinsi") provinsi: RequestBody,
        @Part fotoKtp: MultipartBody.Part
    ): Response<String>
}