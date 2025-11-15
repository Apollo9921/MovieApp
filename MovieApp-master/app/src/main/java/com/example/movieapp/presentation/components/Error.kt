package com.example.movieapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.movieapp.core.Typography
import com.example.movieapp.core.Background
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun ErrorScreen(
    errorMessage: String?,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20,  screenMetrics).sp
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = "$errorMessage",
            textAlign = TextAlign.Center
        )
    }
}