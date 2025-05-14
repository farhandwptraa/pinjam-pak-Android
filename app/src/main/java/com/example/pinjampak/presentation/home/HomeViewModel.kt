package com.example.pinjampak.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.utils.SharedPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _isCustomerDataComplete = MutableStateFlow(false)
    val isCustomerDataComplete: StateFlow<Boolean> = _isCustomerDataComplete

    init {
        checkCustomerDataStatus()
    }

    private fun checkCustomerDataStatus() {
        _isCustomerDataComplete.value = sharedPrefManager.getCustomerId()?.isNotEmpty() == true
    }

    fun updateCustomerDataStatus() {
        checkCustomerDataStatus()
    }
}