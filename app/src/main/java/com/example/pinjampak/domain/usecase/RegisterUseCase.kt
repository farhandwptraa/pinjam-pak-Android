package com.example.pinjampak.domain.usecase

import com.example.pinjampak.data.remote.dto.RegisterRequest
import com.example.pinjampak.data.remote.dto.RegisterResponse
import com.example.pinjampak.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Result<RegisterResponse> {
        return repository.register(request)
    }
}