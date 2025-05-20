package com.example.pinjampak.data.remote.dto

data class PinjamanResponse(
    val idPinjaman: String,
    val amount: Int,
    val status: String,
    val tanggalPencairan: String,
    val bunga: Double
)