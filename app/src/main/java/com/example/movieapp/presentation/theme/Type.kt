package com.example.movieapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun getTypography(fontScale: Float): Typography {
    return Typography(
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * fontScale).sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = (15 * fontScale).sp
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (18 * fontScale).sp
        )
    )
}

@Composable
fun MaterialTheme.toTextColor() = colorScheme.primary