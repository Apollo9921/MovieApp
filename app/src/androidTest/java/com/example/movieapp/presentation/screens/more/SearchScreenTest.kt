package com.example.movieapp.presentation.screens.more

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import com.example.movieapp.presentation.viewModel.SearchMoviesViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val navController = mockk<NavController>(relaxed = true)
    private val screenViewModel = mockk<ScreenSizingViewModel>(relaxed = true)
    private val viewModel = mockk<SearchMoviesViewModel>(relaxed = true)
    private val screenMetrics = ScreenSizingViewModel.ScreenMetrics(0.dp, 0.dp, 0)

    private val fakeMovies = listOf(
        MovieData(
            adult = false,
            backdropPath = "/test.jpg",
            genreIds = listOf(1),
            id = 1,
            originalLanguage = "en",
            originalTitle = "Oppenheimer",
            overview = "",
            popularity = 0.0,
            posterPath = "/test.jpg",
            releaseDate = "",
            title = "Oppenheimer",
            video = false,
            voteAverage = 8.5,
            voteCount = 1000,
            page = 1
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
            voteCount = 1000,
            page = 1
        )
    )

    private fun launchSearchScreen(
        uiState: SearchMoviesViewModel.SearchMovieUiState
    ) {
        composeTestRule.setContent {
            SearchScreen(
                uiState = uiState,
                navController = navController,
                backStack = { navController.navigateUp() },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel,
                viewModel = viewModel
            )
        }
    }

    @Test
    fun searchScreen_whenSuccess_showsMoviesList() {
        // --- ARRANGE ---
        val successState = SearchMoviesViewModel.SearchMovieUiState(
            isLoading = false,
            isSuccess = true,
            isError = false,
            errorMessage = null,
            moviesList = fakeMovies
        )

        // --- ACT ---
        launchSearchScreen(uiState = successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("MoviesContent").assertIsDisplayed()
    }

    @Test
    fun searchScreen_whenFailed_showErrorComponent() {
        // --- ARRANGE ---
        val errorState = SearchMoviesViewModel.SearchMovieUiState(
            isLoading = false,
            isSuccess = false,
            isError = true,
            errorMessage = "Error message",
            moviesList = emptyList()
        )

        // --- ACT ---
        launchSearchScreen(uiState = errorState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("ErrorComponent").assertIsDisplayed()
    }

    @Test
    fun searchScreen_whenLoading_showLoadingComponent() {
        // --- ARRANGE ---
        val loadingState = SearchMoviesViewModel.SearchMovieUiState(
            isLoading = true,
            isSuccess = false,
            isError = false,
            errorMessage = null,
            moviesList = emptyList()
        )

        // --- ACT ---
        launchSearchScreen(uiState = loadingState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("LoadingComponent").assertIsDisplayed()
    }
}