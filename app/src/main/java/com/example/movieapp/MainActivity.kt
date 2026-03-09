package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.movieapp.presentation.navigation.Navigation
import com.example.movieapp.presentation.theme.MovieAppTheme
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.data.repository.NetworkConnectivityObserver
import com.example.movieapp.presentation.viewModel.AccessibilityViewModel
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

val LocalConnectivityStatus = staticCompositionLocalOf { ConnectivityObserver.Status.Unavailable }

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                val accessibilityViewModel = koinViewModel<AccessibilityViewModel>()
                val isHighContrast = accessibilityViewModel.isHighContrastEnabled.collectAsState()
                val fontScale = accessibilityViewModel.fontScale.collectAsState()
                MovieAppTheme(
                    highContrast = isHighContrast.value,
                    fontScale = fontScale.value
                ) {
                    val context = LocalContext.current
                    val localConnectivityObserver = remember { NetworkConnectivityObserver(context) }
                    val currentStatus: State<ConnectivityObserver.Status> = localConnectivityObserver.observe()
                        .collectAsState(initial = ConnectivityObserver.Status.Unavailable)

                    CompositionLocalProvider(LocalConnectivityStatus provides currentStatus.value) {
                        Navigation()
                    }
                }
            }
        }
    }
}