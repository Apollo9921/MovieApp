package com.example.movieapp.utils

import java.text.DecimalFormat

fun formatVoteCount(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val millions = count.toDouble() / 1_000_000.0
            val formatter = if (millions % 1 == 0.0) DecimalFormat("#M") else DecimalFormat("#.#M")
            formatter.format(millions)
        }
        count >= 1_000 -> {
            val thousands = count.toDouble() / 1_000.0
            val formatter = if (thousands % 1 == 0.0) DecimalFormat("#K") else DecimalFormat("#.#K")
            formatter.format(thousands)
        }
        else -> count.toString()
    }
}
