package com.example.pinjampak.data.remote.dto

import com.example.pinjampak.utils.LoanLevel
import com.google.gson.annotations.SerializedName

data class CustomerResponse(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("nik") val nik: String,
    @SerializedName("tempat_lahir") val tempatLahir: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String,
    @SerializedName("pekerjaan") val pekerjaan: String,
    @SerializedName("gaji") val gaji: Double,
    @SerializedName("plafond") val plafond: Double,
    @SerializedName("sisa_plafond") val sisaPlafond: Double,
    @SerializedName("no_hp") val noHp: String,
    @SerializedName("nama_ibu_kandung") val namaIbuKandung: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("provinsi") val provinsi: String,
    @SerializedName("namaCabang") val namaCabang: String,
    @SerializedName("areaCabang") val areaCabang: String,
    @SerializedName("loanLevel") val loanLevel: LoanLevel
)