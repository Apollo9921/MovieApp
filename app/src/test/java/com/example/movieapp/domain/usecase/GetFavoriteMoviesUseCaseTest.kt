package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetFavoriteMoviesUseCaseTest {

    private val repository = mockk<MoviesRepository>()
    private val useCase = GetFavoriteMoviesUseCase(repository)

    @Test
    fun `invoke should return success when repository returns data`() = runBlocking {
        // --- ARRANGE ---
        val fakeMovies = listOf(
            MovieData(
                id = 1,
                title = "Matrix",
                posterPath = "",
                voteAverage = 9.0,
                voteCount = 0,
                releaseDate = "",
                overview = "",
                popularity = 0.0,
                backdropPath = "",
                genreIds = emptyList(),
                originalLanguage = "",
                originalTitle = "",
                adult = false,
                video = false,
                page = 1
            ),
            MovieData(
                id = 2,
                title = "",
                posterPath = "",
                voteAverage = 8.8,
                voteCount = 0,
                releaseDate = "",
                overview = "",
                popularity = 0.0,
                backdropPath = "",
                genreIds = emptyList(),
                originalLanguage = "",
                originalTitle = "",
                adult = false,
                video = false,
                page = 1
            )
        )

        coEvery { repository.getFavoriteMovies() } returns fakeMovies

        // --- ACT ---
        val result = useCase().first()

        // --- ASSERT ---
        assertTrue("Success result", result.isSuccess)
        assertEquals(fakeMovies, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runBlocking {
        // --- ARRANGE ---
        val expectedError = RuntimeException("Room Database Error")

        coEvery { repository.getFavoriteMovies() } throws expectedError

        // --- ACT ---
        val result = useCase().first()

        // --- ASSERT ---
        assertTrue("Failed result", result.isFailure)
        assertEquals(expectedError.message, result.exceptionOrNull()?.message)
    }
}