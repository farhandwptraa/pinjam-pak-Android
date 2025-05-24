package com.example.pinjampak.utils

enum class LoanLevel(
    val plafonRatio: Double,
    val maxTenor: Int,
    val tenorRates: Map<Int, Double>
) {
    LEVEL_1(
        0.2,
        3,
        mapOf(
            1 to 0.04,
            2 to 0.045,
            3 to 0.05
        )
    ),
    LEVEL_2(
        0.4,
        6,
        mapOf(
            1 to 0.035,
            2 to 0.04,
            3 to 0.045,
            6 to 0.055
        )
    ),
    LEVEL_3(
        0.7,
        12,
        mapOf(
            1 to 0.03,
            2 to 0.035,
            3 to 0.04,
            6 to 0.05,
            12 to 0.06
        )
    ),
    LEVEL_4(
        1.0,
        24,
        mapOf(
            1 to 0.025,
            2 to 0.03,
            3 to 0.035,
            6 to 0.045,
            12 to 0.055,
            24 to 0.065
        )
    );
}
