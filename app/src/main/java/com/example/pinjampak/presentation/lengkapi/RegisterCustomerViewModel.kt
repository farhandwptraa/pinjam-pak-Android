package com.example.pinjampak.presentation.lengkapi

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import com.example.pinjampak.domain.repository.RegisterCustomerRepository
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterCustomerViewModel @Inject constructor(
    private val repository: RegisterCustomerRepository,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    var ktpUri: Uri? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    fun registerCustomer(request: RegisterCustomerRequest, ktpFile: File) {
        viewModelScope.launch {
            _isLoading.value = true

            Log.d("RegisterVM", "📤 Mulai proses registrasi customer")
            Log.d("RegisterVM", "📦 File path: ${ktpFile.path}")
            Log.d("RegisterVM", "📦 Request JSON: $request")

            val token = sharedPrefManager.getToken()
            if (token == null) {
                _eventFlow.emit(UiEvent.ShowError("Token tidak ditemukan"))
                Log.e("RegisterVM", "❌ Token tidak ditemukan")
                _isLoading.value = false
                return@launch
            }

            Log.d("RegisterVM", "🔑 Token ditemukan: $token")

            val result = repository.registerCustomer(token, request, ktpFile)
            _isLoading.value = false

            result
                .onSuccess {
                    Log.d("RegisterVM", "✅ Registrasi berhasil")
                    _eventFlow.emit(UiEvent.Success("Registrasi berhasil"))
                }
                .onFailure {
                    Log.e("RegisterVM", "❌ Registrasi gagal: ${it.localizedMessage}", it)
                    _eventFlow.emit(UiEvent.ShowError(it.localizedMessage ?: "Terjadi kesalahan"))
                }
        }
    }

    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        data class Success(val message: String) : UiEvent()
    }
}