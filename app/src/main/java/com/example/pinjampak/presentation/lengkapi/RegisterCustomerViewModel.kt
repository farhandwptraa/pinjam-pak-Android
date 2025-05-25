package com.example.pinjampak.presentation.lengkapi

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.ProvinsiResponse
import com.example.pinjampak.data.remote.dto.RegisterCustomerRequest
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.domain.repository.RegisterCustomerRepository
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterCustomerViewModel @Inject constructor(
    private val repository: RegisterCustomerRepository,
    private val sharedPrefManager: SharedPrefManager,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    var ktpUri: Uri? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    private val _provinsiList = MutableStateFlow<List<ProvinsiResponse>>(emptyList())
    val provinsiList: StateFlow<List<ProvinsiResponse>> = _provinsiList.asStateFlow()

    private val _kotaList = MutableStateFlow<List<String>>(emptyList())
    val kotaList: StateFlow<List<String>> = _kotaList.asStateFlow()

    fun registerCustomer(request: RegisterCustomerRequest, ktpFile: File) {
        viewModelScope.launch {
            _isLoading.value = true

            val token = sharedPrefManager.getToken()
            if (token == null) {
                _eventFlow.emit(UiEvent.ShowError("Token tidak ditemukan"))
                _isLoading.value = false
                return@launch
            }

            val result = repository.registerCustomer(token, request, ktpFile)
            _isLoading.value = false

            result
                .onSuccess {
                    sharedPrefManager.saveCustomerId(request.nik)
                    _eventFlow.emit(UiEvent.Success("Registrasi berhasil"))
                }
                .onFailure {
                    _eventFlow.emit(UiEvent.ShowError(it.localizedMessage ?: "Terjadi kesalahan"))
                }
        }
    }

    fun loadProvinsi() {
        viewModelScope.launch {
            try {
                _provinsiList.value = profileRepository.getAllProvinces()
            } catch (_: Exception) {}
        }
    }

    fun loadKota(provinsiId: Long) {
        viewModelScope.launch {
            try {
                _kotaList.value = profileRepository.getCitiesByProvince(provinsiId)
            } catch (_: Exception) {}
        }
    }

    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        data class Success(val message: String) : UiEvent()
    }
}