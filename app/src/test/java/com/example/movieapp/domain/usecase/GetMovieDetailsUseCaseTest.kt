package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMovieDetailsUseCaseTest {

    private val repository = mockk<MoviesRepository>()
    private val useCase = GetMovieDetailsUseCase(repository)

    @Test
    fun `invoke should return success when repository returns data`() = runBlocking {
        val movieId = 123
        val fakeMovieDetails = MovieDetails(
            adult = true,
            backdropPath = "/hpXBJxLD2SEf8l2CspmSeiHrBKX",
            genres = listOf(),
            id = 1,
            originalLanguage = "en",
            originalTitle = "Frankenstein",
            overview = "Dr. Victor Frankenstein, a brilliant but egotistical scientist, brings a creature to",
            popularity = 529.4985,
            posterPath = "/",
            releaseDate = "2025-10-17",
            title = "Frankenstein",
            video = false,
            voteAverage = 7.84,
            voteCount = 1493,
            belongsToCollection = 0,
            budget = 0,
            homepage = "",
            imdbId = "",
            productionCompanies = emptyList(),
            productionCountries = emptyList(),
            revenue = 0,
            runtime = 0,
            spokenLanguages = emptyList(),
            status = "",
            tagline = "",
            originCountry = emptyList()
        )

        coEvery { repository.getMovieDetails(movieId) } returns fakeMovieDetails
        val result = useCase(movieId).first()

        assertTrue("Success result", result.isSuccess)
        assertEquals(fakeMovieDetails, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runBlocking {
        val movieId = 123
        val expectedError = RuntimeException("API Error")

        coEvery { repository.getMovieDetails(movieId) } throws expectedError
        val result = useCase(movieId).first()

        assertTrue("Failed result", result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }

}