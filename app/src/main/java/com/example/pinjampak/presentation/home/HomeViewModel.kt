package com.example.pinjampak.presentation.home

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.LoanLevel
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _isCustomerDataComplete = MutableStateFlow(false)
    val isCustomerDataComplete: StateFlow<Boolean> = _isCustomerDataComplete.asStateFlow()

    private val _plafonMax = MutableStateFlow(0.0)
    val plafonMax: StateFlow<Double> = _plafonMax.asStateFlow()

    private val _plafonSisa = MutableStateFlow(0.0)
    val plafonSisa: StateFlow<Double> = _plafonSisa.asStateFlow()

    var jumlahPinjaman: String by mutableStateOf("")
    var tenor: Int by mutableStateOf(3)

    var lokasi: String by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set
    var submitResult by mutableStateOf<String?>(null)
        private set

    var loanLevel by mutableStateOf(LoanLevel.LEVEL_4)
        private set

    val availableTenors: List<Int>
        get() = loanLevel.tenorRates.keys.toList().sorted()

    init {
        refreshCustomerStatus()
        loadPlafonData()
    }

    private fun refreshCustomerStatus() {
        _isCustomerDataComplete.value = sharedPrefManager
            .getCustomerId()
            ?.isNotEmpty() == true
    }

    fun updateCustomerDataStatus() = refreshCustomerStatus()

    private fun loadPlafonData() {
        viewModelScope.launch {
            sharedPrefManager.getUsername()?.let { username ->
                profileRepository.getCachedCustomerProfile(username)?.let { profile ->
                    _plafonMax.value = profile.plafond
                    _plafonSisa.value = profile.sisaPlafond
                    loanLevel = profile.loanLevel
                }
            }
        }
    }

    fun setLokasiDariGPS(context: Context, location: Location) {
        if (!Geocoder.isPresent()) {
            Log.e("HomeViewModel", "Geocoder tidak tersedia di perangkat ini.")
            lokasi = "Lokasi tidak diketahui"
            return
        }

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses?.firstOrNull()
            lokasi = if (address != null) {
                "${address.locality}, ${address.adminArea}"
            } else {
                "Lokasi tidak diketahui"
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Gagal mengambil lokasi: ${e.message}")
            lokasi = "Lokasi tidak diketahui"
        }
    }

    fun calculateSimulasi(jumlah: Double, tenor: Int): Pair<Double, Double> {
        val bungaRate = loanLevel.tenorRates[tenor] ?: 0.02
        val totalBayar = jumlah * (1 + bungaRate * tenor)
        val cicilan = if (tenor > 0) totalBayar / tenor else 0.0
        return totalBayar to cicilan
    }

    fun submitPengajuan() {
        val amount = jumlahPinjaman.toIntOrNull() ?: run {
            submitResult = "Jumlah tidak valid"
            return
        }
        if (amount <= 0 || tenor <= 0) {
            submitResult = "Jumlah dan tenor harus lebih dari 0"
            return
        }

        viewModelScope.launch {
            isLoading = true
            submitResult = null
            val lokasi = lokasi
            val success = profileRepository.ajukanPinjaman(amount, tenor, lokasi)
            isLoading = false
            submitResult = if (success) "Pengajuan berhasil" else "Pengajuan gagal"
        }
    }
}