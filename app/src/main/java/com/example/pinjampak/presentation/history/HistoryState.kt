package com.example.pinjampak.presentation.history

import com.example.pinjampak.data.remote.dto.PengajuanResponse
import com.example.pinjampak.data.remote.dto.PinjamanResponse

data class HistoryState(
    val isLoading: Boolean = false,
    val pengajuanList: List<PengajuanResponse> = emptyList(),
    val pinjamanList: List<PinjamanResponse> = emptyList(),
    val error: String? = null
)
