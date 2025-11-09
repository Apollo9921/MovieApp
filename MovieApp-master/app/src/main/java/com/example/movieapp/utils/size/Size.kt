package com.example.movieapp.utils.size

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.movieapp.viewModel.ScreenSizingViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenMetrics(): ScreenSizingViewModel.ScreenMetrics {
    val configuration = LocalConfiguration.current

    return remember(configuration) {
        ScreenSizingViewModel.ScreenMetrics(
            screenWidth = configuration.screenWidthDp.dp,
            screenHeight = configuration.screenHeightDp.dp,
            orientation = configuration.orientation
        )
    }
}