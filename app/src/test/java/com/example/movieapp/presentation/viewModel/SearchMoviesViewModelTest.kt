package com.example.movieapp.presentation.viewModel

import android.util.Log
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetSearchUseCase
import com.example.movieapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.ConnectException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchMoviesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // rule for testing coroutines

    private val getSearchUseCase = mockk<GetSearchUseCase>()
    private val connectivityObserver = mockk<ConnectivityObserver>()

    private lateinit var viewModel: SearchMoviesViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        // ignore the logs
        every { Log.e(any(), any()) } returns 0

        // simulate the connectivity status is available
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
    }

    @Test
    fun `search movies success`() = runTest {
        // --- ARRANGE ---
        val query = "title"
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
                    voteCount = 100,
                    page = 1
                ),
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
                    voteCount = 100,
                    page = 1
                )
            )
        )

        coEvery { getSearchUseCase(query) } returns flowOf(Result.success(fakeMovies))

        viewModel = SearchMoviesViewModel(getSearchUseCase, connectivityObserver)

        // --- ACT---
        viewModel.onQueryChanged(query)

        // Advance until the coroutine finishes
        advanceUntilIdle()

        // --- ASSERT ---
        val uiState = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", uiState.isLoading)
        // error should be false
        assertFalse("The state should not be error", uiState.isError)
        // errorMessage should be null
        assertEquals(null, uiState.errorMessage)
        // success should be true
        assertTrue("The state should be true", uiState.isSuccess)
        // search response should be equal
        assertEquals(fakeMovies.results, uiState.moviesList)
    }

    @Test
    fun `search movies failure`() = runTest {
        // --- ARRANGE ---
        val query = "test to fail"
        val errorMessage = RuntimeException(Constants.UNKNOWN_ERROR)

        coEvery { getSearchUseCase(query) } returns flowOf(Result.failure(errorMessage))

        viewModel = SearchMoviesViewModel(getSearchUseCase, connectivityObserver)

        // --- ACT ---
        viewModel.onQueryChanged(query)

        // Advance until the coroutine finishes
        advanceUntilIdle()

        // --- ASSERT ---
        val uiState = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", uiState.isLoading)
        // success should be false
        assertFalse("The state should not be success", uiState.isSuccess)
        // error should be true
        assertTrue("The state should be error", uiState.isError)
        // error message should not be null
        assertEquals(errorMessage.message, uiState.errorMessage)
    }

    @Test
    fun `search movies success but data is empty`() = runTest {
        // --- ARRANGE ---
        val query = "this query it will not found any movie"
        val fakeMovies = Movies(
            page = 0, totalPages = 0, totalResults = 0, results = emptyList()
        )

        coEvery { getSearchUseCase(query) } returns flowOf(Result.success(fakeMovies))

        viewModel = SearchMoviesViewModel(getSearchUseCase, connectivityObserver)

        // --- ACT ---
        viewModel.onQueryChanged(query)

        // Advance until the coroutine finishes
        advanceUntilIdle()

        // --- ASSERT ---
        val uiState = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", uiState.isLoading)
        // success should be false
        assertFalse("The state should not be success", uiState.isSuccess)
        // error should be true
        assertTrue("The state should be error", uiState.isError)
        // error message should not be null
        assertEquals(Constants.NO_MOVIES_FOUND, uiState.errorMessage)
    }

    @Test
    fun `search movies but in the meantime it occurs not having internet connection`() = runTest {
        // --- ARRANGE ---
        val query = "try find movie"
        val errorMessage = ConnectException(Constants.NO_INTERNET_CONNECTION)

        coEvery { getSearchUseCase(query) } returns flowOf(Result.failure(errorMessage))

        viewModel = SearchMoviesViewModel(getSearchUseCase, connectivityObserver)

        // --- ACT ---
        viewModel.onQueryChanged(query)

        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Unavailable)

        advanceUntilIdle()

        // --- ASSERT ---
        val uiState = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", uiState.isLoading)
        // success should be false
        assertFalse("The state should not be success", uiState.isSuccess)
        // error should be true
        assertTrue("The state should be error", uiState.isError)
        // error message should not be null
        assertEquals(errorMessage.message, uiState.errorMessage)
    }

}