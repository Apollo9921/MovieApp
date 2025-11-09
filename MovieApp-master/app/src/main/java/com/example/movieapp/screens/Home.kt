package com.example.movieapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.movieapp.viewModel.MoviesViewModel
import com.example.movieapp.components.ErrorScreen
import com.example.movieapp.components.MoviesList
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.components.TopBar
import com.example.movieapp.core.Background
import com.example.movieapp.viewModel.ScreenSizingViewModel
import org.koin.androidx.compose.koinViewModel

private var moviesViewModel: MoviesViewModel? = null
private var isConnected = mutableStateOf(false)

@Composable
fun HomeScreen(
    navController: NavController,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    moviesViewModel = koinViewModel<MoviesViewModel>()
    val networkStatus = moviesViewModel?.networkStatus?.collectAsState()
    if (networkStatus?.value == ConnectivityObserver.Status.Available && !isConnected.value) {
        isConnected.value = true
        fetchMovies()
    }

    val isLoading = moviesViewModel?.isLoading?.value
    val isSuccess = moviesViewModel?.isSuccess?.value
    val isError = moviesViewModel?.isError?.value
    val errorMessage = moviesViewModel?.errorMessage?.value

    val moviesList = moviesViewModel?.moviesList ?: ArrayList()
    val genresList = moviesViewModel?.genresList
    val filteredMovies = moviesViewModel?.filteredMovies ?: emptyList()
    val genreType = moviesViewModel?.genreType?.intValue ?: 0

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                stringResource(R.string.home),
                isBack = false,
                backStack = { false },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                screenMetrics,
                screenViewModel
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
            ) {
                when {
                    isLoading == true || isSuccess == true -> {
                        MoviesList(
                            it,
                            moviesList,
                            genresList,
                            filteredMovies,
                            genreType,
                            moviesViewModel!!,
                            navController,
                            screenMetrics,
                            screenViewModel
                        )
                        if (errorMessage == stringResource(R.string.no_internet_connection) && moviesList.isNotEmpty()) {
                            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG)
                                .show()
                            moviesViewModel?.errorMessage?.value = ""
                        }
                    }

                    isError == true -> {
                        isConnected.value = false
                        ErrorScreen(errorMessage, screenMetrics, screenViewModel)
                    }

                    networkStatus?.value == ConnectivityObserver.Status.Unavailable -> {
                        ErrorScreen(
                            stringResource(R.string.no_internet_connection),
                            screenMetrics,
                            screenViewModel
                        )
                    }
                }
            }
        }
    )
}

private fun fetchMovies() {
    CoroutineScope(Dispatchers.IO).launch {
        moviesViewModel?.fetchMovies()
    }
}