package com.example.pinjampak.presentation.login

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.ErrorResponse
import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginWithGoogleRequest
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.SharedPrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharedPrefManager: SharedPrefManager,
    private val profileRepository: ProfileRepository,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun onUsernameChange(username: String) {
        _loginState.update { it.copy(username = username) }
    }

    fun onPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun login() {
        _loginState.update { it.copy(isLoading = true, error = "") }

        viewModelScope.launch {
            try {
                // 🔁 Ambil FCM token dengan coroutine
                val fcmToken = FirebaseMessaging.getInstance().token.await()

                val request = LoginRequest(
                    usernameOrEmail = _loginState.value.username,
                    password = _loginState.value.password,
                    fcmToken = fcmToken
                )

                val response = try {
                    authRepository.login(request)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    throw Exception(errorResponse.message)
                }

                // 🔒 Cek email verified
                if (!response.emailVerified) {
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            error = "Akun belum diverifikasi. Silakan cek email Anda.",
                            isLoggedIn = false
                        )
                    }
                    return@launch
                }

                // ✅ Simpan data ke SharedPreferences
                with(sharedPrefManager) {
                    saveToken(response.token)
                    saveUsername(response.username)
                    saveCustomerId(response.customerId ?: "")
                    saveFcmToken(fcmToken)
                }

                // 📥 Cache profil
                profileRepository.fetchAndCacheUserProfile()
                profileRepository.fetchAndCacheCustomerProfile()

                _loginState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = "",
                        role = response.role,
                        roleId = response.roleId,
                        customerId = response.customerId
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}")
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Terjadi kesalahan"
                    )
                }
            }
        }
    }

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun onGoogleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                _loginState.update { it.copy(error = "Gagal membuka Google Sign-In") }
                return
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken ?: throw Exception("ID Token kosong")

                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    sharedPrefManager.saveFcmToken(fcmToken)
                    viewModelScope.launch {
                        try {
                            val request = LoginWithGoogleRequest(idToken, fcmToken)
                            val response = authRepository.loginWithGoogle(request)

                            if (!response.emailVerified) {
                                _loginState.update {
                                    it.copy(
                                        error = "Akun belum diverifikasi. Silakan cek email Anda.",
                                        isLoggedIn = false
                                    )
                                }
                                return@launch
                            }

                            with(sharedPrefManager) {
                                saveToken(response.token)
                                saveUsername(response.username)
                                saveCustomerId(response.customerId ?: "")
                            }

                            profileRepository.fetchAndCacheUserProfile()
                            profileRepository.fetchAndCacheCustomerProfile()

                            _loginState.update {
                                it.copy(
                                    isLoggedIn = true,
                                    error = "",
                                    role = response.role,
                                    roleId = response.roleId,
                                    customerId = response.customerId
                                )
                            }
                        } catch (e: Exception) {
                            _loginState.update { it.copy(error = e.message ?: "Login dengan Google gagal") }
                        }
                    }
                }.addOnFailureListener {
                    _loginState.update { it.copy(error = "Gagal mengambil FCM token") }
                }
            } catch (e: ApiException) {
                _loginState.update { it.copy(error = "Google Sign-In gagal: ${e.message}") }
            }
        }
    }
}