package com.example.pinjampak.data.remote.dto

data class RegisterCustomerRequest(
    val nik: String,
    val tempat_lahir: String,
    val tanggal_lahir: String,
    val pekerjaan: String,
    val gaji: Int,
    val no_hp: String,
    val nama_ibu_kandung: String,
    val alamat: String,
    val provinsi: String
)

