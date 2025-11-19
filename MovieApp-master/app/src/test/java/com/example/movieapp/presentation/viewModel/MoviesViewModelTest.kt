package com.example.movieapp.presentation.viewModel

import android.util.Log
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MoviesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMoviesUseCase = mockk<GetMoviesUseCase>()
    private val getGenresUseCase = mockk<GetGenresUseCase>()
    private val connectivityObserver = mockk<ConnectivityObserver>()

    private lateinit var viewModel: MoviesViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.v(any(), any()) } returns 0
    }

    @Test
    fun `fetchMovies should update state to success when use cases return data`() = runTest {
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)

        val fakeMovies = Movies(
            page = 1,
            totalPages = 1,
            totalResults = 2,
            results = listOf(
                MovieData(
                    adult = true,
                    id = 1,
                    backdropPath = "/hpXBJxLD2SEf8l2CspmSeiHrBKX",
                    genreIds = listOf(18, 27, 14),
                    originalLanguage = "en",
                    originalTitle = "Frankenstein",
                    overview = "Dr. Victor Frankenstein, a brilliant but egotistical scientist, brings a creature to",
                    popularity = 529.4985,
                    posterPath = "/g4JtvGlQO7DByTI6frUobqvSL3R",
                    releaseDate = "2025-10-17",
                    title = "Frankenstein",
                    video = false,
                    voteAverage = 7.84,
                    voteCount = 1493
                ),
                MovieData(
                    adult = true,
                    id = 1,
                    backdropPath = "/hpXBJxLD2SEf8l2CspmSeiHrBKX",
                    genreIds = listOf(18, 27, 14),
                    originalLanguage = "en",
                    originalTitle = "Test",
                    overview = "test overview",
                    popularity = 645.4985,
                    posterPath = "/g4JtvGlQO7DByTI6frUobqvSL3R",
                    releaseDate = "2025-10-17",
                    title = "Test",
                    video = false,
                    voteAverage = 7.84,
                    voteCount = 2000
                )
            )
        )
        val fakeGenres = GenresList(
            genres = listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 12, name = "Adventure"),
                Genre(id = 16, name = "Animation"),
                Genre(id = 35, name = "Comedy"),
                Genre(id = 80, name = "Crime"),
                Genre(id = 99, name = "Documentary"),
                Genre(id = 18, name = "Drama")
            )
        )

        coEvery { getMoviesUseCase(any()) } returns flowOf(Result.success(fakeMovies))
        coEvery { getGenresUseCase() } returns flowOf(Result.success(fakeGenres))
        viewModel = MoviesViewModel(getMoviesUseCase, getGenresUseCase, connectivityObserver)
        viewModel.fetchMovies()

        val currentState = viewModel.uiState.value

        assertFalse("End Loading State", currentState.isLoading)
        assertTrue("State is Success", currentState.isSuccess)
        assertEquals(fakeMovies.results, currentState.movies)
        assertEquals(fakeGenres, currentState.genres)
    }
}