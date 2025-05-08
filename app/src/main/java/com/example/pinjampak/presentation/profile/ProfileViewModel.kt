// ProfileViewModel.kt
package com.example.pinjampak.presentation.profile

import androidx.lifecycle.ViewModel
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {
    fun logout() {
        sharedPrefManager.clear()
    }
}