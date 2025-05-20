package com.example.pinjampak.data.remote.dto

data class PengajuanResponse(
    val idPengajuan: String,
    val amount: Int,
    val status: String,
    val tanggalPengajuan: String,
    val catatanMarketing: String?
)