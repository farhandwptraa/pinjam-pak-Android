package com.example.pinjampak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_profile")
data class CustomerProfileEntity(
    @PrimaryKey val username: String,
    val email: String,
    val namaLengkap: String,
    val nik: String,
    val tempatLahir: String,
    val tanggalLahir: String,
    val pekerjaan: String,
    val gaji: Double,
    val plafond: Double,
    val sisaPlafond: Double,
    val noHp: String,
    val namaIbuKandung: String,
    val alamat: String,
    val provinsi: String,
    val namaCabang: String,
    val areaCabang: String
)