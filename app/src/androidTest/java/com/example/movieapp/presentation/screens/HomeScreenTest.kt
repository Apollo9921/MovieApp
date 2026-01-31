package com.example.movieapp.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.viewModel.MoviesViewModel
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val navController = mockk<NavController>(relaxed = true)
    private val screenViewModel = mockk<ScreenSizingViewModel>(relaxed = true)
    private val screenMetrics = ScreenSizingViewModel.ScreenMetrics(0.dp, 0.dp, 0)

    private val fakeMovies = listOf(
        MovieData(
            adult = false,
            backdropPath = "/teste.jpg",
            genreIds = listOf(1),
            id = 1,
            originalLanguage = "en",
            originalTitle = "Oppenheimer",
            overview = "",
            popularity = 0.0,
            posterPath = "/teste.jpg",
            releaseDate = "",
            title = "Oppenheimer",
            video = false,
            voteAverage = 8.5,
            voteCount = 1000,
            page = 1
        ),
        MovieData(
            adult = false,
            backdropPath = "/teste.jpg",
            genreIds = listOf(1),
            id = 1,
            originalLanguage = "en",
            originalTitle = "Barbie",
            overview = "",
            popularity = 0.0,
            posterPath = "/teste2.jpg",
            releaseDate = "",
            title = "Barbie",
            video = false,
            voteAverage = 8.5,
            voteCount = 1000,
            page = 1
        )
    )

    private fun launchHomeScreen(uiState: MoviesViewModel.MoviesUiState) {
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel,
                fetchMovies = {},
                genreSelected = {},
                navController = navController
            )
        }
    }

    @Test
    fun homeScreen_whenSuccess_showsMoviesList() {
        // --- ARRANGE ---
        val successState = MoviesViewModel.MoviesUiState(
            isLoading = false,
            isSuccess = true,
            movies = fakeMovies
        )

        // --- ACT ---
        launchHomeScreen(uiState = successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("MoviesContent").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Oppenheimer").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Barbie").assertIsDisplayed()
    }

    @Test
    fun homeScreen_whenLoading_showsLoadingComponent() {
        // --- ARRANGE ---
        val loadingState = MoviesViewModel.MoviesUiState(
            isLoading = true,
            movies = emptyList()
        )

        // --- ACT ---
        launchHomeScreen(uiState = loadingState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("LoadingComponent").assertIsDisplayed()
    }

    @Test
    fun homeScreen_whenError_showErrorComponent() {
        // --- ARRANGE ---
        val errorState = MoviesViewModel.MoviesUiState(
            isLoading = false,
            error = true,
            errorMessage = "Error message",
            isSuccess = false
        )

        // --- ACT ---
        launchHomeScreen(uiState = errorState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("ErrorComponent").assertIsDisplayed()
    }

    @Test
    fun homeScreen_whenNoMoviesFound_butAlreadyHasMovies_showsMoviesList() {
        // --- ARRANGE ---
        val noMoreMovies = Exception(Constants.ERROR_FETCHING_MOVIES)

        val errorState = MoviesViewModel.MoviesUiState(
            isLoading = false,
            error = true,
            errorMessage = noMoreMovies.message,
            isSuccess = true,
            movies = fakeMovies
        )

        // --- ACT ---
        launchHomeScreen(uiState = errorState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("MoviesContent").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Oppenheimer").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Barbie").assertIsDisplayed()
    }
}