package com.example.movieapp.presentation.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.presentation.components.ErrorScreen
import com.example.movieapp.presentation.components.FavouritesListComponent
import com.example.movieapp.presentation.components.LoadingScreen
import com.example.movieapp.presentation.components.TopBar
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.utils.TopBarAction
import com.example.movieapp.presentation.viewModel.FavoritesViewModel
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesRoute(
    navController: NavController,
    backStack: () -> Boolean,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val genreTypeSelected = viewModel.genreTypeSelected.collectAsState().value
    val refresh = { viewModel.getFavoriteMovies() }
    val updatePosition = { viewModel.updateMoviePosition() }
    val genreClicked = { it: Int -> viewModel.onGenreTypeSelected(it) }

    FavoritesScreen(
        uiState = uiState,
        genreTypeSelected = genreTypeSelected,
        navController = navController,
        backStack = { backStack() },
        screenMetrics = screenMetrics,
        screenViewModel = screenViewModel,
        onRefresh = { refresh() },
        onMove = { from, to -> viewModel.moveMovie(from, to) },
        updateMoviePosition = { updatePosition() },
        onGenreClick = genreClicked
    )

}

@Composable
fun FavoritesScreen(
    uiState: FavoritesViewModel.FavoritesMoviesUiState,
    genreTypeSelected: FavoritesViewModel.GenresState,
    navController: NavController,
    backStack: () -> Boolean,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    onRefresh: () -> Unit,
    onMove: (Int, Int) -> Unit,
    updateMoviePosition: () -> Unit,
    onGenreClick: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                stringResource(R.string.favourites),
                backStack = { backStack() },
                action = TopBarAction.Favorite(
                    iconRes = R.drawable.drag,
                    onClick = {  }
                ),
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel,
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Background)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.testTag("LoadingComponent")) {
                            LoadingScreen()
                        }
                    }

                    uiState.isSuccess -> {
                        Box(modifier = Modifier.testTag("FavouritesComponent")) {
                            FavouritesListComponent(
                                movieData = uiState.moviesList,
                                genreTypeSelected = genreTypeSelected,
                                filterMovies = uiState.filteredMovies,
                                genresList = uiState.genresList,
                                onGenreClick = { genreId -> onGenreClick(genreId) },
                                screenMetrics = screenMetrics,
                                screenViewModel = screenViewModel,
                                onMove = { from, to -> onMove(from, to) },
                                updateMoviePosition = { updateMoviePosition() }
                            )
                        }
                    }

                    uiState.isError -> {
                        Box(modifier = Modifier.testTag("ErrorComponent")) {
                            ErrorScreen(
                                errorMessage = uiState.errorMessage!!,
                                screenMetrics = screenMetrics,
                                screenViewModel = screenViewModel,
                                onRefresh = { onRefresh() }
                            )
                        }
                    }
                }
            }
        }
    )
}