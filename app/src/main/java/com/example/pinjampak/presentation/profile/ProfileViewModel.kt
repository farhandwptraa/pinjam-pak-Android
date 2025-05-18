package com.example.pinjampak.presentation.profile

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
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    var userProfile by mutableStateOf<UserProfileEntity?>(null)
        private set

    var customerProfile by mutableStateOf<CustomerProfileEntity?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            isLoading = true
            Log.d("ProfileViewModel", "Loading profiles...")

            // Fetch and cache user profile
            try {
                Log.d("ProfileViewModel", "Fetching user profile...")
                val user = repository.fetchAndCacheUserProfile()
                userProfile = user
                if (user != null) {
                    Log.d("ProfileViewModel", "User profile fetched: $user")

                    // Fetch and cache customer profile if user profile is available
                    try {
                        Log.d("ProfileViewModel", "Fetching customer profile...")
                        customerProfile = repository.fetchAndCacheCustomerProfile()
                        Log.d("ProfileViewModel", "Customer profile fetched: $customerProfile")
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error fetching customer profile: ${e.message}")
                        customerProfile = null // Customer profile belum lengkap
                    }
                } else {
                    Log.d("ProfileViewModel", "User profile is null")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user profile: ${e.message}")
            }

            isLoading = false
            Log.d("ProfileViewModel", "Profiles loading finished. User: $userProfile, Customer: $customerProfile")
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val token = sharedPrefManager.getToken()
                val fcmToken = sharedPrefManager.getFcmToken()

                if (!token.isNullOrBlank()) {
                    repository.logout(token, fcmToken)
                    Log.d("ProfileViewModel", "Logout berhasil ke server.")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Logout error: ${e.message}")
            }

            sharedPrefManager.clear()
            Log.d("ProfileViewModel", "User logged out and session cleared.")
        }
    }
}
