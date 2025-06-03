package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.movieapp.navigation.BasicNavigation
import com.example.movieapp.core.MovieAppTheme
import com.example.movieapp.utils.network.ConnectivityObserver
import com.example.movieapp.utils.network.NetworkConnectivityObserver

private lateinit var connectivityObserver: ConnectivityObserver
lateinit var status: ConnectivityObserver.Status

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                connectivityObserver = NetworkConnectivityObserver(LocalContext.current)
                status = connectivityObserver.observe()
                    .collectAsState(initial = ConnectivityObserver.Status.Unavailable).value
                BasicNavigation()
            }
        }
    }
}