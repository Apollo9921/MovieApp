package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsMovieFavoriteUseCaseTest {

    private val repository = mockk<MoviesRepository>()
    private val useCase = IsMovieFavoriteUseCase(repository)

    @Test
    fun `invoke should return success when repository returns data`() = runBlocking {
        // --- ARRANGE ---
        val movieId = 123
        val isFavorite = true

        coEvery { repository.isMovieFavorite(movieId) } returns isFavorite

        // --- ACT ---
        val result = useCase(movieId).first()

        // --- ASSERT ---
        assertTrue("Success result", result.isSuccess)
        assertEquals(isFavorite, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runBlocking {
        // --- ARRANGE ---
        val movieId = 123
        val expectedError = RuntimeException("Room Database Error")

        coEvery { repository.isMovieFavorite(movieId) } throws expectedError

        // --- ACT ---
        val result = useCase(movieId).first()

        // --- ASSERT ---
        assertTrue("Failed result", result.isFailure)
        assertEquals(expectedError.message, result.exceptionOrNull()?.message)
    }
}