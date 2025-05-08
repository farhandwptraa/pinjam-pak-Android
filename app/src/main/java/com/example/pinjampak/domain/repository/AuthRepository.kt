package com.example.pinjampak.domain.repository

import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): LoginResponse
}