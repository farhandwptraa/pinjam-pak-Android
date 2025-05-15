package com.example.pinjampak.presentation.password

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.SharedPrefManager
import com.example.pinjampak.data.local.entity.UserProfileEntity
import com.example.pinjampak.data.local.entity.CustomerProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.domain.repository.AuthRepository
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun sendForgotPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.forgotPassword(email)
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Gagal mengirim permintaan")
            }
        }
    }
}