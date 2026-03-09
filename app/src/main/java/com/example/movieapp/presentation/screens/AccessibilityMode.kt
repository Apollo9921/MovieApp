package com.example.movieapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.presentation.components.TopBar
import com.example.movieapp.presentation.theme.toTextColor
import com.example.movieapp.presentation.viewModel.AccessibilityViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccessibilityRoute(
    navController: NavController,
    viewModel: AccessibilityViewModel = koinViewModel()
) {
    val isHighContrast = viewModel.isHighContrastEnabled.collectAsState()
    val onHighContrastToggled = { it: Boolean -> viewModel.onHighContrastToggled(it) }
    val fontScale = viewModel.fontScale.collectAsState()
    val onFontScaleSlide = { it: Float -> viewModel.onFontScaleChanged(it) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                stringResource(R.string.accessibility_mode),
                isBack = true,
                backStack = { navController.navigateUp() }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(it)
            ) {
                AccessibilityOptions(
                    isHighContrast = isHighContrast,
                    onHighContrastToggled = onHighContrastToggled,
                    fontScale = fontScale,
                    onFontScaleSlide = onFontScaleSlide
                )
            }
        }
    )
}

@Composable
private fun AccessibilityOptions(
    isHighContrast: State<Boolean>,
    onHighContrastToggled: (Boolean) -> Unit,
    fontScale: State<Float>,
    onFontScaleSlide: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(vertical = 16.dp, horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.accessibility),
            color = MaterialTheme.toTextColor(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.high_contrast_mode),
                color = MaterialTheme.toTextColor(),
                style = MaterialTheme.typography.labelMedium
            )
            Switch(
                checked = isHighContrast.value,
                onCheckedChange = { onHighContrastToggled(it) }
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Text(
            text = stringResource(R.string.font_scale),
            color = MaterialTheme.toTextColor(),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(fontScale.value * 100).toInt()}%",
            color = MaterialTheme.toTextColor(),
            style = MaterialTheme.typography.labelMedium
        )
        Slider(
            value = fontScale.value,
            onValueChange = { onFontScaleSlide(it) },
            valueRange = 0.8f..1.5f,
            steps = 6
        )
    }
}