package com.example.pinjampak.utils

import android.content.Context
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
}