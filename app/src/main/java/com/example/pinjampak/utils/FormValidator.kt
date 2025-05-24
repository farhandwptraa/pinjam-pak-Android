package com.example.pinjampak.utils

fun validateRegisterForm(
    username: String,
    password: String,
    confirmPassword: String,
    email: String,
    namaLengkap: String
): String? {
    return when {
        username.isBlank() || email.isBlank() || namaLengkap.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
            "Semua field harus diisi"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
            "Email tidak valid"
        password.length < 6 ->
            "Password minimal 6 karakter"
        password != confirmPassword ->
            "Konfirmasi password tidak cocok"
        else -> null
    }
}