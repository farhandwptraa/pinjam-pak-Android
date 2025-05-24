package com.example.pinjampak.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.RegisterRequest
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun register(username: String, password: String, email: String, namaLengkap: String) {
        _state.value = RegisterState(isLoading = true)

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "", success = false)

            val request = RegisterRequest(username, password, email, namaLengkap)
            val result = authRepository.register(request)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isLoading = false, success = true)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Registrasi gagal")
                }
            )
        }
    }
}