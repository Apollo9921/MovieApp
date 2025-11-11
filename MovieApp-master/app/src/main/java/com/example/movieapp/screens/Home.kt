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
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.components.LoadingScreen
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
    val uiState = moviesViewModel?.uiState?.collectAsState()

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
                    uiState?.value?.isLoading == true && uiState.value.movies.isEmpty() -> {
                        LoadingScreen()
                    }

                    uiState?.value?.isSuccess == true -> {
                        val moviesList = moviesViewModel?.moviesList ?: ArrayList()
                        val genresList = moviesViewModel?.genresList
                        val filteredMovies = moviesViewModel?.filteredMovies ?: emptyList()
                        val genreType = moviesViewModel?.genreType?.intValue ?: 0
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
                        if (uiState.value.errorMessage == stringResource(R.string.no_internet_connection)) {
                            Toast.makeText(
                                LocalContext.current,
                                uiState.value.errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            uiState.value.errorMessage = null
                        }
                    }

                    uiState?.value?.error == true -> {
                        isConnected.value = false
                        ErrorScreen(uiState.value.errorMessage, screenMetrics, screenViewModel)
                    }
                }
            }
        }
    )
}