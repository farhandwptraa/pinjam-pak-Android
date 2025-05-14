package com.example.pinjampak.presentation.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun onUsernameChange(username: String) {
        _loginState.update { it.copy(username = username) }
    }

    fun onPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            try {
                val request = LoginRequest(
                    usernameOrEmail = _loginState.value.username,
                    password = _loginState.value.password
                )
                val response = authRepository.login(request)

                // ✅ Simpan token, username, role_id, dan customerId
                sharedPrefManager.saveToken(response.token)
                sharedPrefManager.saveUsername(response.username)
                sharedPrefManager.saveCustomerId(response.customerId ?: "") // aman walaupun null

                _loginState.update {
                    it.copy(
                        isLoggedIn = true,
                        error = ""
                    )
                }

            } catch (e: Exception) {
                _loginState.update {
                    it.copy(error = e.message ?: "Login failed")
                }
            }
        }
    }
}