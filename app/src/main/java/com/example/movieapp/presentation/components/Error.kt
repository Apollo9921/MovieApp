package com.example.movieapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.movieapp.R
import com.example.movieapp.core.Constants
import com.example.movieapp.presentation.theme.Black
import com.example.movieapp.presentation.theme.White
import com.example.movieapp.presentation.theme.toTextColor

@Composable
fun ErrorScreen(
    errorMessage: String?,
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
            .background(MaterialTheme.colorScheme.onBackground),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = message,
                color = MaterialTheme.toTextColor(),
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
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(R.string.refresh),
                        color = MaterialTheme.toTextColor(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}