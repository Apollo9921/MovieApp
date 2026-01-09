package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMoviesUseCaseTest {

    private val repository = mockk<MoviesRepository>()

    private val getMoviesUseCase = GetMoviesUseCase(repository)

    @Test
    fun `invoke should return success list when repository returns data`() = runBlocking {
        // --- ARRANGE ---
        val page = 1
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
                    voteCount = 1493,
                    page = 1
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
                    voteCount = 2000,
                    page = 1
                )
            )
        )
        coEvery { repository.fetchMovies(page, emptyList()) } returns fakeMovies

        // --- ACT ---
        val result = getMoviesUseCase(page, emptyList()).first()

        // --- ASSERT ---
        assertTrue("Success result", result.isSuccess)
        assertEquals(fakeMovies, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runBlocking {
        // --- ARRANGE ---
        val page = 1
        val expectedError = RuntimeException("API Error")
        coEvery { repository.fetchMovies(page, emptyList()) } throws expectedError

        // --- ACT ---
        val result = getMoviesUseCase(page ,emptyList()).first()

        // --- ASSERT ---
        assertTrue("Failed result", result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }
}