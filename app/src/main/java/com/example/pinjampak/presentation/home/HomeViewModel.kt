package com.example.pinjampak.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _isCustomerDataComplete = MutableStateFlow(false)
    val isCustomerDataComplete: StateFlow<Boolean> = _isCustomerDataComplete.asStateFlow()

    private val _plafonMax = MutableStateFlow(0.0)
    val plafonMax: StateFlow<Double> = _plafonMax.asStateFlow()

    private val _plafonSisa = MutableStateFlow(0.0)
    val plafonSisa: StateFlow<Double> = _plafonSisa.asStateFlow()

    var jumlahPinjaman: String by mutableStateOf("")
    var tenor: Int by mutableStateOf(6)

    init {
        refreshCustomerStatus()
        loadPlafonData()
    }

    private fun refreshCustomerStatus() {
        _isCustomerDataComplete.value = sharedPrefManager.getCustomerId()?.isNotEmpty() == true
    }

    fun updateCustomerDataStatus() = refreshCustomerStatus()

    private fun loadPlafonData() {
        viewModelScope.launch {
            sharedPrefManager.getUsername()?.let { username ->
                repository.getCachedCustomerProfile(username)?.let { profile ->
                    _plafonMax.value = profile.plafond
                    _plafonSisa.value = profile.sisaPlafond
                }
            }
        }
    }

    fun calculateSimulasi(jumlah: Double, tenor: Int): Pair<Double, Double> {
        val bungaRate = 0.02
        val totalBayar = jumlah * (1 + bungaRate * tenor)
        val cicilan = if (tenor > 0) totalBayar / tenor else 0.0
        return totalBayar to cicilan
    }

    fun submitPengajuan() {
        // TODO: implementasi pengajuan via repository dan handle hasilnya
    }
}