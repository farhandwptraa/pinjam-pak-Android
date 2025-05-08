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
}