package com.example.pinjampak.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        val token = sharedPrefManager.getToken() ?: return
        val bearerToken = "Bearer $token"

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val pengajuan = apiService.getPengajuanHistory(bearerToken)
                val pinjaman = apiService.getPinjamanHistory(bearerToken)

                _state.value = HistoryState(
                    pengajuanList = pengajuan,
                    pinjamanList = pinjaman,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Terjadi kesalahan"
                )
            }
        }
    }
}
