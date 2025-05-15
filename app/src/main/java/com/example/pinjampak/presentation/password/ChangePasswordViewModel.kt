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
import kotlinx.coroutines.launch

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.changePassword(oldPassword, newPassword)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal mengubah password")
            }
        }
    }
}