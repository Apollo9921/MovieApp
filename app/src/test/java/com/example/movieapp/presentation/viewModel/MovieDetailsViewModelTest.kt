package com.example.movieapp.presentation.viewModel

import android.util.Log
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.details.FormattedMovieDetails
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.details.ProductionCompany
import com.example.movieapp.domain.model.details.SpokenLanguage
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.FormatMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import com.example.movieapp.domain.usecase.IsMovieFavoriteUseCase
import com.example.movieapp.domain.usecase.ToggleFavoriteUseCase
import com.example.movieapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // rule for testing coroutines

    private val getMovieDetailsUseCase = mockk<GetMovieDetailsUseCase>()
    private val formatMovieDetailsUseCase = mockk<FormatMovieDetailsUseCase>()
    private val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>()
    private val isMovieFavoriteUseCase = mockk<IsMovieFavoriteUseCase>()
    private val connectivityObserver = mockk<ConnectivityObserver>()

    private lateinit var viewModel: MovieDetailsViewModel

    private val movieDetails = MovieDetails(
        adult = false,
        backdropPath = "/backdrop.jpg",
        genres = listOf(),
        id = 123,
        originalLanguage = "en",
        originalTitle = "Original Title",
        overview = "Overview",
        popularity = 0.0,
        posterPath = "/poster.jpg",
        releaseDate = "2023-01-01",
        title = "Title",
        video = false,
        voteAverage = 7.5,
        voteCount = 10,
        runtime = 120,
        spokenLanguages = listOf(SpokenLanguage("English", "", "English")),
        productionCompanies = listOf(ProductionCompany(1, "", "A Company", "")),
        productionCountries = listOf(),
        budget = 1000000,
        revenue = 2000000,
        homepage = "https://example.com",
        status = "Released",
        tagline = "Tagline",
        imdbId = "tt1234567",
        belongsToCollection = "",
        originCountry = emptyList()
    )

    private val expectedFormattedDetails = FormattedMovieDetails(
        title = "Title",
        overview = "Overview",
        posterUrl = "https://image.tmdb.org/t/p/w500/poster.jpg",
        voteAverage = "7.5",
        voteCount = "10",
        releaseYear = "2023",
        genres = "Action, Drama",
        runtime = "2h 0m",
        spokenLanguages = listOf("English"),
        productionCompanies = listOf("A Company")
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        // ignore the logs
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        // simulate the connectivity status is available
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
    }

    @Test
    fun `fetch movie details success`() = runTest {
        // --- ARRANGE ---
        coEvery { getMovieDetailsUseCase(any()) } returns flowOf(Result.success(movieDetails))

        val expectedFormattedDetails = FormattedMovieDetails(
            title = "Title",
            overview = "Overview",
            posterUrl = "https://image.tmdb.org/t/p/w500/poster.jpg",
            voteAverage = "7.5",
            voteCount = "10",
            releaseYear = "2023",
            genres = "Action, Drama",
            runtime = "2h 0m",
            spokenLanguages = listOf("English"),
            productionCompanies = listOf("A Company")
        )
        every { formatMovieDetailsUseCase.invoke(any()) } returns expectedFormattedDetails
        every { formatMovieDetailsUseCase.checkIfMovieDetailsNotEmpty(expectedFormattedDetails) } returns true
        every { isMovieFavoriteUseCase.invoke(any()) } returns flowOf(Result.success(false))


        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            formatMovieDetailsUseCase,
            toggleFavoriteUseCase,
            isMovieFavoriteUseCase,
            connectivityObserver
        )

        // --- ACT---
        viewModel.uiState.value.movieId = 123

        advanceUntilIdle()

        // --- ASSERT ---
        val formattedMovieDetails = formatMovieDetailsUseCase(movieDetails)
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // error should be false
        assertFalse("The state should not be error", state.error)
        // errorMessage should be null
        assertEquals(null, state.errorMessage)
        // isSuccess should be true
        assertEquals(true, state.isSuccess)
        // movieDetails should be the formatted movie details
        assertEquals(formattedMovieDetails, state.movieDetails)
    }

    @Test
    fun `fetch movie details failure`() = runTest {
        // --- ARRANGE ---
        val error = RuntimeException(Constants.UNKNOWN_ERROR)
        coEvery { getMovieDetailsUseCase(any()) } returns flowOf(Result.failure(error))

        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            formatMovieDetailsUseCase,
            toggleFavoriteUseCase,
            isMovieFavoriteUseCase,
            connectivityObserver
        )

        // --- ACT---
        viewModel.uiState.value.movieId = 123

        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // success should be false
        assertFalse("Should not be success", state.isSuccess)
        // error should be true
        assertTrue("The state should be error", state.error)
        // error message should not be null
        assertEquals(error.message, state.errorMessage)
    }

    @Test
    fun `fetch movie details without internet connection`() = runTest {
        // --- ARRANGE ---
        val errorMessage = Constants.NO_INTERNET_CONNECTION
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Unavailable)

        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            formatMovieDetailsUseCase,
            toggleFavoriteUseCase,
            isMovieFavoriteUseCase,
            connectivityObserver
        )

        // --- ACT ---
        viewModel.uiState.value.movieId = 123

        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // success should be false
        assertFalse("Should not be success", state.isSuccess)
        // error should be true
        assertTrue("The state should be error", state.error)
        // error message should not be null
        assertEquals(errorMessage, state.errorMessage)
    }

    @Test
    fun `fetch movie details success but data is empty`() = runTest {
        // --- ARRANGE ---
        val movieDetailsEmpty = movieDetails.copy(
            title = "",
            overview = "",
            voteAverage = 0.0,
            voteCount = 0,
            releaseDate = "",
            genres = emptyList(),
            runtime = 0,
            spokenLanguages = emptyList(),
            productionCompanies = emptyList()
        )
        val expectedFormattedMovieDetailsEmpty = FormattedMovieDetails(
            title = "",
            overview = "",
            posterUrl = "https://image.tmdb.org/t/p/w500/poster.jpg",
            voteAverage = "",
            voteCount = "",
            releaseYear = "",
            genres = "",
            runtime = "",
            spokenLanguages = emptyList(),
            productionCompanies = emptyList()
        )

        coEvery { getMovieDetailsUseCase(any()) } returns flowOf(Result.success(movieDetailsEmpty))
        every { formatMovieDetailsUseCase.invoke(any()) } returns expectedFormattedMovieDetailsEmpty
        every {
            formatMovieDetailsUseCase.checkIfMovieDetailsNotEmpty(
                expectedFormattedMovieDetailsEmpty
            )
        } returns false


        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            formatMovieDetailsUseCase,
            toggleFavoriteUseCase,
            isMovieFavoriteUseCase,
            connectivityObserver
        )

        // --- ACT---
        viewModel.uiState.value.movieId = 123

        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // isSuccess should be false
        assertEquals(false, state.isSuccess)
        // error should be true
        assertTrue("The state should be error", state.error)
        // error message should not be null
        assertEquals(Constants.NO_INFO_AVAILABLE, state.errorMessage)
    }

    @Test
    fun `toggle movie should add movie to favorites successfully`() = runTest {
        // --- ARRANGE ---
        coEvery { toggleFavoriteUseCase(any(), false) } returns flowOf(Result.success(Unit))
        coEvery { isMovieFavoriteUseCase(123) } returns flowOf(Result.success(false))
        every { formatMovieDetailsUseCase.invoke(any()) } returns expectedFormattedDetails

        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            formatMovieDetailsUseCase,
            toggleFavoriteUseCase,
            isMovieFavoriteUseCase,
            connectivityObserver
        )

        // --- ACT ---
        viewModel.uiState.value.movieId = 123
        viewModel.uiState.value.movieDetails = expectedFormattedDetails
        viewModel.uiState.value.isFavorite = false

        viewModel.toggleMovie()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        assertTrue(state.isFavorite)
    }

    @Test
    fun `toggle movie should remove movie to favorites successfully`() = runTest {
        // --- ARRANGE ---
        coEvery { toggleFavoriteUseCase(any(), true) } returns flowOf(Result.success(Unit))
        coEvery { isMovieFavoriteUseCase(123) } returns flowOf(Result.success(true))
        every { formatMovieDetailsUseCase.invoke(any()) } returns expectedFormattedDetails


        viewModel = MovieDetailsViewModel(
            getMovieDetailsUseCase,
            formatMovieDetailsUseCase,
            toggleFavoriteUseCase,
            isMovieFavoriteUseCase,
            connectivityObserver
        )

        // --- ACT ---
        viewModel.uiState.value.movieId = 123
        viewModel.uiState.value.movieDetails = expectedFormattedDetails
        viewModel.uiState.value.isFavorite = true

        viewModel.toggleMovie()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        assertEquals(false, state.isFavorite)
    }
}