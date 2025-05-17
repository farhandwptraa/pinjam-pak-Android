package com.example.pinjampak.utils

enum class LoanLevel(
    val level: Int,
    val maxBungaPercent: Double,
    val maxTenorMonths: Int
) {
    LEVEL1(1, 1.5, 3),
    LEVEL2(2, 1.2, 6),
    LEVEL3(3, 1.0, 12),
    LEVEL4(4, 0.8, 24);

    fun description(): String =
        "Level $level. Max bunga ${maxBungaPercent}% dengan maksimal tenor ${maxTenorMonths} bulan"
}
