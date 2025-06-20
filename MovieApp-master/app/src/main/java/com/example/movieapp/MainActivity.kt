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
import com.example.movieapp.navigation.BasicNavigation
import com.example.movieapp.core.MovieAppTheme
import com.example.movieapp.utils.network.ConnectivityObserver
import com.example.movieapp.utils.network.NetworkConnectivityObserver
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

val LocalConnectivityStatus = staticCompositionLocalOf { ConnectivityObserver.Status.Unavailable }

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                MovieAppTheme {
                    val context = LocalContext.current
                    val localConnectivityObserver = remember { NetworkConnectivityObserver(context) }
                    val currentStatus: State<ConnectivityObserver.Status> = localConnectivityObserver.observe()
                        .collectAsState(initial = ConnectivityObserver.Status.Unavailable)

                    CompositionLocalProvider(LocalConnectivityStatus provides currentStatus.value) {
                        BasicNavigation()
                    }
                }
            }
        }
    }
}