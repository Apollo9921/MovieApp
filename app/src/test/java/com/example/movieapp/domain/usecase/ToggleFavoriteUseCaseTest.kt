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

class ToggleFavoriteUseCaseTest {

    private val repository = mockk<MoviesRepository>()
    private val useCase = ToggleFavoriteUseCase(repository)

    private val fakeMovie =
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
            video = false
        )

    @Test
    fun `invoke should return success when repository returns data`() = runBlocking {
        // --- ARRANGE ---
        coEvery { repository.toggleFavoriteMovie(fakeMovie, false) } returns Unit

        // --- ACT ---
        val result = useCase(fakeMovie, false).first()

        // --- ASSERT ---
        assertTrue("Success result", result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runBlocking {
        // --- ARRANGE ---
        val expectedError = RuntimeException("Room Database Error")

        coEvery { repository.toggleFavoriteMovie(fakeMovie, false) } throws expectedError

        // --- ACT ---
        val result = useCase(fakeMovie, false).first()

        // --- ASSERT ---
        assertTrue("Failed result", result.isFailure)
        assertEquals(expectedError.message, result.exceptionOrNull()?.message)
    }

}