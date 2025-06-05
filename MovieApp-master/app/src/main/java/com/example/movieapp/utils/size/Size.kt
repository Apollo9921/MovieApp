package com.example.movieapp.utils.size

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ScreenSizeUtils {

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    @ReadOnlyComposable
    fun getScreenWidthDp(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp.dp
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    @ReadOnlyComposable
    fun getScreenHeightDp(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp.dp
    }

    @Composable
    fun calculateCustomWidth(baseSize: Int): Int {
        val screenWidth = getScreenWidthDp()
        val smallWidth = 600.dp
        val mediumWidth = 840.dp
        return when {
            screenWidth < smallWidth -> {
                baseSize
            }
            screenWidth < mediumWidth -> {
                (baseSize * 1.5).toInt()
            }
            else -> {
                (baseSize * 2)
            }
        }
    }

    @Composable
    fun calculateCustomHeight(baseSize: Int): Int {
        val screenHeight = getScreenHeightDp()
        val smallHeight = 950.dp
        val mediumHeight = 1400.dp

        return when {
            screenHeight < smallHeight -> {
                baseSize
            }

            screenHeight < mediumHeight -> {
                (baseSize * 1.5).toInt()
            }

            else -> {
                (baseSize * 2)
            }
        }
    }
}