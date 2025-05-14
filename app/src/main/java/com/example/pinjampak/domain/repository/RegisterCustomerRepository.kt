package com.example.pinjampak.domain.repository

import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import java.io.File

interface RegisterCustomerRepository {
    suspend fun registerCustomer(
        token: String,
        request: RegisterCustomerRequest,
        ktpImageFile: File
    ): Result<String>
}