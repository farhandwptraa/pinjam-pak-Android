package com.example.pinjampak.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.RegisterRequest
import com.example.pinjampak.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun register(username: String, password: String, email: String, namaLengkap: String) {
        _state.value = RegisterState(isLoading = true)

        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    username = username,
                    password = password,
                    email = email,
                    nama_lengkap = namaLengkap
                )

                val result = registerUseCase(request)

                result
                    .onSuccess {
                        _state.value = RegisterState(success = true)
                    }
                    .onFailure { e ->
                        _state.value = RegisterState(error = e.message ?: "Terjadi kesalahan saat registrasi")
                    }

            } catch (e: Exception) {
                _state.value = RegisterState(error = e.message ?: "Terjadi kesalahan")
            }
        }
    }
}