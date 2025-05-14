package com.example.pinjampak.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("pinjampak_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        pref.edit().putString("token", token).apply()
    }

    fun getToken(): String? = pref.getString("token", null)

    fun clear() {
        pref.edit().clear().apply()
    }

    // Fungsi lain yang mengakses SharedPreferences
    fun getRoleId(): String? = pref.getString("role_id", null)

    // Menambah fungsi untuk mengambil dan menyimpan username
    fun saveUsername(username: String) {
        pref.edit().putString("username", username).apply()
    }

    fun getUsername(): String? = pref.getString("username", null)

    fun saveCustomerId(customerId: String) {
        pref.edit().putString("customerId", customerId).apply()
    }

    fun getCustomerId(): String? {
        return pref.getString("customerId", null)
    }
}
