package com.example.pinjampak.utils

import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPrefHelper: SharedPrefHelper) {

    fun saveToken(token: String) {
        sharedPrefHelper.saveToken(token)
    }

    fun getToken(): String? {
        return sharedPrefHelper.getToken()
    }

    fun clear() {
        sharedPrefHelper.clear()
    }

    // Menambah fungsi lain sesuai kebutuhan
    fun isUserLoggedIn(): Boolean {
        return getToken() != null
    }

    // Menambah fungsi getUsername
    fun getUsername(): String? {
        return sharedPrefHelper.getUsername()
    }

    // Fungsi untuk menyimpan username
    fun saveUsername(username: String) {
        sharedPrefHelper.saveUsername(username)
    }
}
