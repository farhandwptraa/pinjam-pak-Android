package com.example.pinjampak.data.repository

import com.example.pinjampak.data.remote.api.AuthApi
import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginResponse
import com.example.pinjampak.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(request: LoginRequest): LoginResponse {
        return authApi.login(request)
    }
}