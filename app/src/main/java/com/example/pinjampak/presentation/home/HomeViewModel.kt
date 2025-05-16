package com.example.pinjampak.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _isCustomerDataComplete = MutableStateFlow(false)
    val isCustomerDataComplete: StateFlow<Boolean> = _isCustomerDataComplete

    var plafonMax by mutableStateOf(0.0)
        private set

    var plafonSisa by mutableStateOf(0.0)
        private set

    var jumlahPinjaman by mutableStateOf("")
    var tenor by mutableStateOf(6)

    init {
        checkCustomerDataStatus()
        loadPlafonData()
    }

    private fun checkCustomerDataStatus() {
        _isCustomerDataComplete.value = sharedPrefManager.getCustomerId()?.isNotEmpty() == true
    }

    fun updateCustomerDataStatus() {
        checkCustomerDataStatus()
    }

    fun loadPlafonData() {
        viewModelScope.launch {
            val username = sharedPrefManager.getUsername()
            if (!username.isNullOrEmpty()) {
                repository.getCachedCustomerProfile(username)?.let { data ->
                    plafonMax = data.plafond
                    plafonSisa = data.sisaPlafond
                }
            }
        }
    }

    fun getSimulasi(): Pair<Double, Double> {
        val jumlah = jumlahPinjaman.toDoubleOrNull() ?: 0.0
        val bunga = 0.02
        val totalBayar = jumlah * (1 + bunga * tenor)
        val cicilan = if (tenor > 0) totalBayar / tenor else 0.0
        return totalBayar to cicilan
    }
}