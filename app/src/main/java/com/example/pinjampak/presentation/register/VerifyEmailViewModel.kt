package com.example.pinjampak.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyEmailUiState())
    val uiState: StateFlow<VerifyEmailUiState> = _uiState

    fun verifyEmail(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                repository.verifyEmail(token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Verifikasi berhasil!", // ⬅️ Ganti dengan teks tetap
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = null,
                        errorMessage = e.message ?: "Terjadi kesalahan."
                    )
                }
            }
        }
    }
}

data class VerifyEmailUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
