package com.example.pinjampak.presentation.login

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjampak.data.remote.dto.LoginRequest
import com.example.pinjampak.data.remote.dto.LoginWithGoogleRequest
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.SharedPrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        Log.d(TAG, "Username changed: $username")
    }

    fun onPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
        Log.d(TAG, "Password changed: [PROTECTED]")
    }

    fun login() {
        Log.d(TAG, "Starting normal login")
        viewModelScope.launch {
            try {
                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    Log.d(TAG, "FCM token retrieved: $fcmToken")
                    viewModelScope.launch {
                        try {
                            val request = LoginRequest(
                                usernameOrEmail = _loginState.value.username,
                                password = _loginState.value.password,
                                fcmToken = fcmToken
                            )
                            Log.d(TAG, "Login request prepared: $request")
                            val response = authRepository.login(request)
                            Log.d(TAG, "Login response received: $response")

                            sharedPrefManager.saveToken(response.token)
                            sharedPrefManager.saveUsername(response.username)
                            sharedPrefManager.saveCustomerId(response.customerId ?: "")
                            sharedPrefManager.saveFcmToken(fcmToken)

                            Log.d(TAG, "Token and user info saved in SharedPref")

                            profileRepository.fetchAndCacheUserProfile()
                            profileRepository.fetchAndCacheCustomerProfile()

                            _loginState.update { it.copy(isLoggedIn = true, error = "") }
                            Log.d(TAG, "User logged in successfully")
                        } catch (e: Exception) {
                            Log.d(TAG, "Login failed with exception: ${e.message}")
                            _loginState.update { it.copy(error = e.message ?: "Login failed") }
                        }
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Failed to retrieve FCM token")
                    _loginState.update { it.copy(error = "Gagal mengambil FCM token") }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Login outer exception: ${e.message}")
                _loginState.update { it.copy(error = e.message ?: "Login failed") }
            }
        }
    }

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun onGoogleSignInResult(result: ActivityResult) {
        Log.d(TAG, "onGoogleSignInResult called with resultCode=${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                Log.e(TAG, "Google Sign-In intent is null")
                _loginState.update { it.copy(error = "Gagal membuka Google Sign-In") }
                return
            }
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken
                Log.d(TAG, "Google sign-in success, idToken=$idToken")
                if (idToken == null) {
                    Log.d(TAG, "Google idToken is null")
                    _loginState.update { it.copy(error = "Google idToken kosong") }
                    return
                }

                // Ambil FCM token dulu sebelum request login dengan Google
                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    Log.d(TAG, "FCM token retrieved: $fcmToken")
                    sharedPrefManager.saveFcmToken(fcmToken)

                    viewModelScope.launch {
                        try {
                            val request = LoginWithGoogleRequest(
                                idToken = idToken,
                                fcmToken = fcmToken
                            )
                            val response = authRepository.loginWithGoogle(request)
                            Log.d(TAG, "LoginWithGoogle response: $response")

                            sharedPrefManager.saveToken(response.token)
                            sharedPrefManager.saveUsername(response.username)
                            sharedPrefManager.saveCustomerId(response.customerId ?: "")
                            sharedPrefManager.saveFcmToken(fcmToken)

                            profileRepository.fetchAndCacheUserProfile()
                            profileRepository.fetchAndCacheCustomerProfile()

                            _loginState.update { it.copy(isLoggedIn = true, error = "") }
                            Log.d(TAG, "Google login successful")
                        } catch (e: Exception) {
                            Log.d(TAG, "Login with Google failed: ${e.message}")
                            _loginState.update { it.copy(error = e.message ?: "Login with Google failed") }
                        }
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Failed to retrieve FCM token")
                    _loginState.update { it.copy(error = "Gagal mengambil FCM token") }
                }
            } catch (e: ApiException) {
                Log.d(TAG, "Google sign-in failed: ${e.statusCode}, message: ${e.message}")
                _loginState.update { it.copy(error = "Google sign-in failed: ${e.statusCode}") }
            }
        } else {
            Log.d(TAG, "Google sign-in cancelled or failed, resultCode=${result.resultCode}")
            _loginState.update { it.copy(error = "Google sign-in gagal atau dibatalkan") }
        }
    }
}
