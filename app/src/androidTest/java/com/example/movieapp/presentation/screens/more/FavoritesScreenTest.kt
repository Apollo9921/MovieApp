package com.example.movieapp.presentation.screens.more

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.viewModel.FavoritesViewModel
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val navController = mockk<NavController>(relaxed = true)
    private val screenMetrics = ScreenSizingViewModel.ScreenMetrics(0.dp, 0.dp, 0)
    private val screenViewModel = mockk<ScreenSizingViewModel>(relaxed = true)

    private val fakeMovies = listOf(
        MovieData(
            adult = false,
            backdropPath = "/test.jpg",
            genreIds = listOf(1),
            id = 0,
            originalLanguage = "en",
            originalTitle = "Oppenheimer",
            overview = "",
            popularity = 0.0,
            posterPath = "/test.jpg",
            releaseDate = "",
            title = "Oppenheimer",
            video = false,
            voteAverage = 8.5,
            voteCount = 1000
        ),
        MovieData(
            adult = false,
            backdropPath = "/test.jpg",
            genreIds = listOf(1),
            id = 1,
            originalLanguage = "en",
            originalTitle = "Barbie",
            overview = "",
            popularity = 0.0,
            posterPath = "/test2.jpg",
            releaseDate = "",
            title = "Barbie",
            video = false,
            voteAverage = 8.5,
            voteCount = 1000
        )
    )

    private fun launchFavoritesScreen(uiState: FavoritesViewModel.FavoritesMoviesUiState) {

        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                navController = navController,
                backStack = { navController.navigateUp() },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel,
                onRefresh = { },
                onMove = { from, to -> },
                updateMoviePosition = { }
            )
        }
    }

    @Test
    fun favoritesScreen_whenSuccess_showFavoritesComponent() {
        // --- ARRANGE ---
        val successState = FavoritesViewModel.FavoritesMoviesUiState(
            isLoading = false,
            isSuccess = true,
            isError = false,
            errorMessage = null,
            moviesList = fakeMovies
        )

        // --- ACT ---
        launchFavoritesScreen(uiState = successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("FavouritesComponent").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_whenEmpty_showErrorComponent() {
        // --- ARRANGE ---
        val empty = FavoritesViewModel.FavoritesMoviesUiState(
            isLoading = false,
            isSuccess = false,
            isError = true,
            errorMessage = Constants.NO_MOVIES_FOUND,
            moviesList = emptyList()
        )

        // --- ACT ---
        launchFavoritesScreen(uiState = empty)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("ErrorComponent").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_whenError_showErrorComponent() {
        // --- ARRANGE ---
        val errorState = FavoritesViewModel.FavoritesMoviesUiState(
            isLoading = false,
            isSuccess = false,
            isError = true,
            errorMessage = "Error message",
            moviesList = emptyList()
        )

        // --- ACT ---
        launchFavoritesScreen(uiState = errorState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("ErrorComponent").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_whenLoading_showLoadingComponent() {
        // --- ARRANGE ---
        val loadingState = FavoritesViewModel.FavoritesMoviesUiState(
            isLoading = true,
            isSuccess = false,
            isError = false,
            errorMessage = null,
            moviesList = emptyList()
        )

        // --- ACT ---
        launchFavoritesScreen(uiState = loadingState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("LoadingComponent").assertIsDisplayed()
    }

}