package com.example.movieapp.presentation.viewModel

import android.util.Log
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import com.example.movieapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // rule for testing coroutines

    private val getMoviesUseCase = mockk<GetMoviesUseCase>()
    private val getGenresUseCase = mockk<GetGenresUseCase>()
    private val connectivityObserver = mockk<ConnectivityObserver>()

    private lateinit var viewModel: MoviesViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        // ignore the logs
        every { Log.e(any(), any()) } returns 0

        // simulate the connectivity status is available
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
    }

    @Test
    fun `fetch movies success`() = runTest {
        // --- ARRANGE ---
        val fakeMovies = Movies(
            page = 1, totalPages = 1, totalResults = 1,
            results = listOf(
                MovieData(
                    adult = false,
                    backdropPath = "/backdrop.jpg",
                    genreIds = listOf(1, 2),
                    id = 123,
                    originalLanguage = "en",
                    originalTitle = "Original Title",
                    overview = "Overview",
                    popularity = 1.0,
                    posterPath = "/poster.jpg",
                    releaseDate = "2023-01-01",
                    title = "Title",
                    video = false,
                    voteAverage = 7.5,
                    voteCount = 100
                )
            )
        )
        val fakeGenres = GenresList(listOf(Genre(1, "Action")))

        // Teach the Mocks: When they called, return success with the fake data"
        coEvery { getMoviesUseCase(any()) } returns flowOf(Result.success(fakeMovies))
        coEvery { getGenresUseCase() } returns flowOf(Result.success(fakeGenres))

        viewModel = MoviesViewModel(getMoviesUseCase, getGenresUseCase, connectivityObserver)

        // --- ACT---
        viewModel.fetchMovies()

        // Advance until the coroutine finishes
        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value

        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // error should be false
        assertFalse("The state should not be error", state.error)
        // errorMessage should be null
        assertEquals(null, state.errorMessage)
        // isSuccess should be true
        assertTrue("Should be success", state.isSuccess)
        // movies should be the fake data
        assertEquals(fakeMovies.results, state.movies)
    }

    @Test
    fun `fetch movies failure`() = runTest {
        // --- ARRANGE ---
        val error = RuntimeException(Constants.UNKNOWN_ERROR)

        // Teach the Mocks: When they called, return failure"
        coEvery { getMoviesUseCase(any()) } returns flowOf(Result.failure(error))
        coEvery { getGenresUseCase() } returns flowOf(Result.success(GenresList(emptyList())))

        viewModel = MoviesViewModel(getMoviesUseCase, getGenresUseCase, connectivityObserver)

        // --- ACT---
        viewModel.fetchMovies()

        // Advance until the coroutine finishes
        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value

        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // isSuccess should be false
        assertFalse("Should not be success", state.isSuccess)
        // error should be true
        assertTrue("The state should be error", state.error)
        // error message should not be null
        assertEquals(error.message, state.errorMessage)
        // movies should be empty
        assertEquals(emptyList<MovieData>(), state.movies)
    }

    @Test
    fun `fetch movies loading should be in loading while waiting for data`() = runTest {
        // --- ARRANGE ---
        val fakeMovies = Movies(page = 1, totalPages = 1, totalResults = 1, results = listOf())

        // Teach the Mock to take some time to return the data
        coEvery { getMoviesUseCase(any()) } coAnswers {
            delay(1000L)
            flowOf(Result.success(fakeMovies))
        }
        coEvery { getGenresUseCase() } returns flowOf(Result.success(GenresList(emptyList())))

        viewModel = MoviesViewModel(getMoviesUseCase, getGenresUseCase, connectivityObserver)

        // --- ACT ---
        viewModel.fetchMovies()

        // --- ASSERT ---
        // loading should be true
        assertTrue("The state should be loading", viewModel.uiState.value.isLoading)

        // --- ACT---
        advanceUntilIdle()

        // --- ASSERT ---
        // loading should be false
        assertFalse("The state should not be loading", viewModel.uiState.value.isLoading)
    }

    @Test
    fun `fetch movies but in the meantime occurs not having internet connection`() = runTest {
        // --- Arrange ---
        val error = RuntimeException(Constants.NO_INTERNET_CONNECTION)

        coEvery { getMoviesUseCase(any()) } returns flowOf(Result.failure(error))
        coEvery { getGenresUseCase() } returns flowOf(Result.failure(error))

        viewModel = MoviesViewModel(getMoviesUseCase, getGenresUseCase, connectivityObserver)

        // --- ACT ---
        viewModel.fetchMovies()

        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Unavailable)

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
    fun `fetch movies success but genres failed`() = runTest {
        // --- Assert ---
        val fakeMovies = Movies(page = 1, totalPages = 1, totalResults = 1, results = listOf())
        val genreError = RuntimeException(Constants.UNKNOWN_ERROR)

        coEvery { getMoviesUseCase(any()) } returns flowOf(Result.success(fakeMovies))
        coEvery { getGenresUseCase() } returns flowOf(Result.failure(genreError))

        viewModel = MoviesViewModel(getMoviesUseCase, getGenresUseCase, connectivityObserver)

        // --- ACT ---
        viewModel.fetchMovies()

        advanceUntilIdle()

        // ---ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // success should be false
        assertFalse("Should not be success", state.isSuccess)
        // error should be true
        assertTrue("The state should be error", state.error)
        // error message should not be null
        assertEquals(genreError.message, state.errorMessage)
    }
}