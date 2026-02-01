package com.example.movieapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    background = TopBarBackground,
    onBackground = Background,
    primary = White,
    onError = Red
)

private val DarkColorScheme = darkColorScheme(
    background = TopBarBackground,
    onBackground = Background,
    primary = White,
    onError = Red
)

private val HighContrastColorScheme = darkColorScheme(
    background = Black,
    onBackground = Black,
    primary = BrightYellow,
    onError = BrightYellow
)

@Composable
fun MovieAppTheme(
    highContrast: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast -> HighContrastColorScheme
        else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}