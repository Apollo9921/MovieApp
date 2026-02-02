package com.example.movieapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private val LightColorScheme = lightColorScheme(
    background = TopBarBackground,
    onBackground = Background,
    primary = White,
    secondary = White,
    onError = Red
)

private val DarkColorScheme = darkColorScheme(
    background = TopBarBackground,
    onBackground = Background,
    primary = White,
    secondary = White,
    onError = Red
)

private val HighContrastColorScheme = darkColorScheme(
    background = Black,
    onBackground = BlackHighContrast,
    primary = TextHighContrast,
    secondary = YellowHighContrast,
    onError = IconHighContrast
)

@Composable
fun MovieAppTheme(
    highContrast: Boolean,
    fontScale: Float,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast -> HighContrastColorScheme
        else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    val customTypography = remember(fontScale) {
        getTypography(fontScale)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}