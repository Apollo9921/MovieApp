package com.example.movieapp.presentation.screens.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.domain.model.details.FormattedMovieDetails
import com.example.movieapp.presentation.viewModel.MovieDetailsViewModel
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val navController = mockk<NavController>(relaxed = true)
    private val screenViewModel = mockk<ScreenSizingViewModel>(relaxed = true)
    private val screenMetrics = ScreenSizingViewModel.ScreenMetrics(0.dp, 0.dp, 0)

    val fakeMovieDetails = FormattedMovieDetails(
        title = "Oppenheimer",
        overview = "The story of J. Robert Oppenheimer's role in the development of the atomic bomb during World War II.",
        posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
        voteAverage = "8.5",
        voteCount = "1000",
        releaseYear = "2023",
        genres = "Action, Drama, History",
        runtime = "2h 0m",
        spokenLanguages = listOf("English"),
        productionCompanies = listOf("Warner Bros. Pictures")
    )


    private fun launchDetailsScreen(uiState: MovieDetailsViewModel.MovieDetailsUiState) {
        composeTestRule.setContent {
            DetailsScreen(
                uiState = uiState,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel,
                backStack = { navController.navigateUp() },
                onRefresh = {},
                favoritesClick = {}
            )
        }
    }

    @Test
    fun detailsScreen_whenSuccess_showDetails() {
        // --- ARRANGE ---
        val successState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetails = fakeMovieDetails
        )

        // --- ACT ---
        launchDetailsScreen(successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("SuccessComponent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_whenLoading_showLoadingComponent() {
        // --- ARRANGE ---
        val loadingState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = true,
            isSuccess = false,
            error = false,
            errorMessage = null,
            movieDetails = null
        )

        // --- ACT ---
        launchDetailsScreen(loadingState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("LoadingComponent").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_whenError_showErrorComponent() {
        // --- ARRANGE ---
        val errorState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = false,
            error = true,
            errorMessage = "Error message",
            movieDetails = null
        )

        // --- ACT ---
        launchDetailsScreen(errorState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("ErrorComponent").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_whenSuccess_butSectionDetailsAndRatingEmpty_shouldNotDisplay() {
        // --- ARRANGE ---
        val fakeMovieDetailsEmpty = fakeMovieDetails.copy(
            title = "",
            voteAverage = "",
            voteCount = ""
        )
        val successState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetails = fakeMovieDetailsEmpty
        )

        // --- ACT ---
        launchDetailsScreen(successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("SuccessComponent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionReleaseInfo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionOverview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListLanguages").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListCompanies").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionDetails").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("SectionRating").assertIsNotDisplayed()
    }

    @Test
    fun detailsScreen_whenSuccess_butSectionReleaseInfoEmpty_shouldNotDisplay() {
        // --- ARRANGE ---
        val fakeMovieDetailsEmpty = fakeMovieDetails.copy(
            releaseYear = "",
            genres = "",
            runtime = ""
        )
        val successState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetails = fakeMovieDetailsEmpty
        )

        // --- ACT ---
        launchDetailsScreen(successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("SuccessComponent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionDetails").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionRating").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionOverview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListLanguages").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListCompanies").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionReleaseInfo").assertIsNotDisplayed()
    }

    @Test
    fun detailsScreen_whenSuccess_butSectionOverviewEmpty_shouldNotDisplay() {
        // --- ARRANGE ---
        val fakeMovieDetailsEmpty = fakeMovieDetails.copy(
            overview = ""
        )
        val successState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetails = fakeMovieDetailsEmpty
        )

        // --- ACT ---
        launchDetailsScreen(successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("SuccessComponent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionDetails").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionRating").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListLanguages").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListCompanies").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionOverview").assertIsNotDisplayed()
    }

    @Test
    fun detailsScreen_whenSuccess_butSectionListLanguagesEmpty_shouldNotDisplay() {
        // --- ARRANGE ---
        val fakeMovieDetailsEmpty = fakeMovieDetails.copy(
            spokenLanguages = emptyList()
        )
        val successState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetails = fakeMovieDetailsEmpty
        )

        // --- ACT ---
        launchDetailsScreen(successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("SuccessComponent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionDetails").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionRating").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionOverview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListCompanies").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionReleaseInfo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListLanguages").assertIsNotDisplayed()
    }

    @Test
    fun detailsScreen_whenSuccess_butSectionListCompaniesEmpty_shouldNotDisplay() {
        // --- ARRANGE ---
        val fakeMovieDetailsEmpty = fakeMovieDetails.copy(
            productionCompanies = emptyList()
        )
        val successState = MovieDetailsViewModel.MovieDetailsUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetails = fakeMovieDetailsEmpty
        )

        // --- ACT ---
        launchDetailsScreen(successState)

        // --- ASSERT ---
        composeTestRule.onNodeWithTag("SuccessComponent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionDetails").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionRating").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionOverview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListLanguages").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionReleaseInfo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SectionListCompanies").assertIsNotDisplayed()
    }
}