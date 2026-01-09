package com.example.movieapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.movieapp.presentation.viewModel.MoviesViewModel
import com.example.movieapp.presentation.components.ErrorScreen
import com.example.movieapp.presentation.components.MoviesList
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.presentation.components.BottomNavigationBar
import com.example.movieapp.presentation.components.LoadingScreen
import com.example.movieapp.presentation.components.TopBar
import com.example.movieapp.presentation.navigation.Details
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    navController: NavController,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    moviesViewModel: MoviesViewModel = koinViewModel()
) {
    val uiState = moviesViewModel.uiState.collectAsState().value
    val fetchMovies = { moviesViewModel.fetchMovies() }
    val genreSelected = { id: Int -> moviesViewModel.onGenreTypeSelected(id) }

    HomeScreen(
        uiState = uiState,
        screenMetrics = screenMetrics,
        screenViewModel = screenViewModel,
        navController = navController,
        fetchMovies = fetchMovies,
        genreSelected = genreSelected
    )
}

@Composable
fun HomeScreen(
    uiState: MoviesViewModel.MoviesUiState,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    navController: NavController,
    fetchMovies: () -> Unit,
    genreSelected: (Int) -> Unit
) {
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
                    uiState.isLoading -> {
                        Box(modifier = Modifier.testTag("LoadingComponent")) {
                            LoadingScreen()
                        }
                    }

                    uiState.isSuccess -> {
                        Box(modifier = Modifier.testTag("MoviesContent")) {
                            MoviesList(
                                pv = it,
                                movies = if (uiState.filteredMovies.isNotEmpty() || uiState.genreType != 0) uiState.filteredMovies else uiState.movies,
                                genresList = uiState.genres.genres,
                                selectedGenreId = uiState.genreType,
                                onMovieClick = { movieId ->
                                    navController.navigate(Details(movieId = movieId))
                                },
                                onLoadMore = {
                                    if (uiState.filteredMovies.isEmpty() && uiState.genreType == 0) {
                                        fetchMovies()
                                    }
                                },
                                onGenreClick = { id -> genreSelected(id) },
                                screenMetrics = screenMetrics,
                                screenViewModel = screenViewModel
                            )
                        }
                    }

                    uiState.error && uiState.errorMessage != null -> {
                        Box(modifier = Modifier.testTag("ErrorComponent")) {
                            ErrorScreen(
                                errorMessage = uiState.errorMessage!!,
                                screenMetrics = screenMetrics,
                                screenViewModel = screenViewModel,
                                onRefresh = { fetchMovies() }
                            )
                        }
                    }
                }
            }
        }
    )
}