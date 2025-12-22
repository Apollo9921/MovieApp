package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetFavoritesMoviesCountUseCaseTest {

    private val repository = mockk<MoviesRepository>()
    private val useCase = GetFavoritesMoviesCountUseCase(repository)

    @Test
    fun `invoke should return success when repository returns data`() = runBlocking {
        // --- ARRANGE ---
        val count = 5
        coEvery { repository.getMovieCount() } returns count

        // --- ACT ---
        val result = useCase().first()

        // --- ASSERT ---
        assertTrue("Success result", result.isSuccess)
        assertEquals(count, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runBlocking {
        // --- ARRANGE ---
        val expectedError = RuntimeException("Room Database Error")

        coEvery { repository.getMovieCount() } throws expectedError

        // --- ACT ---
        val result = useCase().first()

        // --- ASSERT ---
        assertTrue("Failed result", result.isFailure)
        assertEquals(expectedError.message, result.exceptionOrNull()?.message)
    }
}