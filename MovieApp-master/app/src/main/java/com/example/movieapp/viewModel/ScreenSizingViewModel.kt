package com.example.movieapp.viewModel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import android.content.res.Configuration

class ScreenSizingViewModel : ViewModel() {

    data class ScreenMetrics(val screenWidth: Dp, val screenHeight: Dp, val orientation: Int)

    fun calculateCustomWidth(baseSize: Int, metrics: ScreenMetrics): Int {
        val screenWidth = metrics.screenWidth
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

    fun calculateCustomHeight(baseSize: Int, metrics: ScreenMetrics): Int {
        val screenHeight = metrics.screenHeight
        val isPortrait = metrics.orientation == Configuration.ORIENTATION_PORTRAIT

        val smallHeight = 950.dp
        val mediumHeight = 1400.dp

        return if (!isPortrait) {
            when {
                screenHeight < smallHeight -> (baseSize * 0.2).toInt()
                screenHeight < mediumHeight -> (baseSize * 0.5).toInt()
                else -> baseSize
            }
        } else {
            when {
                screenHeight < smallHeight -> baseSize
                screenHeight < mediumHeight -> (baseSize * 0.5).toInt()
                else -> (baseSize * 0.2).toInt()
            }
        }
    }
}