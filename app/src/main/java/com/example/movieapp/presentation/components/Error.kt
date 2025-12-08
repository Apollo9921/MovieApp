package com.example.movieapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapp.R
import com.example.movieapp.core.Constants
import com.example.movieapp.presentation.theme.Typography
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.theme.Black
import com.example.movieapp.presentation.theme.White
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun ErrorScreen(
    errorMessage: String?,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    showButton: Boolean = false,
    onRefresh: () -> Unit
) {
    var message = errorMessage
    if (message == null) message = Constants.UNKNOWN_ERROR
    var isButtonToShow = showButton
    if (message != Constants.NO_INTERNET_CONNECTION && message != Constants.NO_MOVIES_FOUND) isButtonToShow = true
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20, screenMetrics).sp
        val buttonSize = screenViewModel.calculateCustomWidth(baseSize = 15, screenMetrics).sp
        Column {
            Text(
                style = Typography.titleLarge.copy(fontSize = titleSize),
                text = message,
                textAlign = TextAlign.Center
            )
            if (isButtonToShow) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRefresh()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black,
                        contentColor = White
                    )
                ) {
                    Text(
                        style = Typography.labelMedium.copy(fontSize = buttonSize),
                        text = stringResource(R.string.refresh),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}