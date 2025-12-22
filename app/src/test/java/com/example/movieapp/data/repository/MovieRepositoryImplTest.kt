package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.MovieDao
import com.example.movieapp.data.network.dto.details.DetailsDTO
import com.example.movieapp.data.network.dto.genres.GenreDTO
import com.example.movieapp.data.network.dto.genres.GenresListDTO
import com.example.movieapp.data.network.dto.movies.MovieDataDTO
import com.example.movieapp.data.network.dto.movies.MoviesDTO
import com.example.movieapp.data.network.service.MovieService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieRepositoryImplTest {

    private val movieService = mockk<MovieService>()
    private val movieDao = mockk<MovieDao>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val repository = MovieRepositoryImpl(movieService, movieDao, testDispatcher)

    @Test
    fun `fetchMovies should call service and return mapped data`() = runTest {
        // --- ARRANGE ---
        val fakeMoviesDTO = MoviesDTO(
            page = 1,
            totalPages = 1,
            totalResults = 1,
            results = listOf(
                MovieDataDTO(
                    id = 101,
                    title = "Test Movie",
                    posterPath = null,
                    adult = null,
                    backdropPath = null,
                    genreIds = null,
                    originalLanguage = null,
                    originalTitle = null,
                    overview = null,
                    popularity = null,
                    releaseDate = null,
                    video = null,
                    voteAverage = null,
                    voteCount = null
                )
            )
        )

        coEvery { movieService.getMovies(1) } returns fakeMoviesDTO

        // --- ACT ---
        val result = repository.fetchMovies(1)

        // --- ASSERT ---
        assertEquals(1, result.results.size)
        assertEquals("", result.results[0].posterPath)
        assertEquals(101, result.results[0].id)
    }

    @Test
    fun `fetchGenres should call service and return mapped data`() = runTest {
        // --- ARRANGE ---
        val fakeGenresDTO = GenresListDTO(
            genres = listOf(
                GenreDTO(
                    id = 101,
                    name = "Test Genre"
                ),
                GenreDTO(
                    id = 102,
                    name = "Test Genre 2"

                )
            )
        )

        coEvery { movieService.getGenres() } returns fakeGenresDTO

        // --- ACT ---
        val result = repository.fetchGenres()

        // --- ASSERT ---
        assertEquals(2, result.genres.size)
        assertEquals("Test Genre", result.genres[0].name)
        assertEquals(102, result.genres[1].id)
    }

    @Test
    fun `searchMovie should call service and return mapped data`() = runTest {
        // --- ARRANGE ---
        val query = "test"
        val fakeMoviesDTO = MoviesDTO(
            page = 1,
            totalPages = 1,
            totalResults = 1,
            results = listOf(
                MovieDataDTO(
                    id = 101,
                    title = "Test Movie",
                    posterPath = null,
                    adult = null,
                    backdropPath = null,
                    genreIds = null,
                    originalLanguage = null,
                    originalTitle = null,
                    overview = null,
                    popularity = null,
                    releaseDate = null,
                    video = null,
                    voteAverage = null,
                    voteCount = null
                )
            )
        )

        coEvery { movieService.searchMovie(query) } returns fakeMoviesDTO

        // --- ACT ---
        val result = repository.searchMovie(query)

        // --- ASSERT ---
        assertEquals(1, result.results.size)
        assertEquals("", result.results[0].posterPath)
        assertEquals(101, result.results[0].id)

    }

    @Test
    fun `getMovieDetails should call service and return mapped data`() = runTest {
        // --- ARRANGE ---
        val id = 23
        val fakeDetailsDTO = DetailsDTO(
            id = 23,
            title = "Test Movie",
            posterPath = null,
            adult = null,
            backdropPath = null,
            genres = null,
            originalLanguage = null,
            originalTitle = null,
            overview = null,
            popularity = null,
            releaseDate = null,
            video = null,
            voteAverage = null,
            voteCount = null,
            budget = null,
            homepage = null,
            imdbId = null,
            productionCompanies = null,
            productionCountries = null,
            revenue = null,
            runtime = null,
            spokenLanguages = null,
            status = null,
            tagline = null,
            originCountry = null,
            belongsToCollection = null
        )

        coEvery { movieService.getMovieDetails(id) } returns fakeDetailsDTO

        // --- ACT ---
        val result = repository.getMovieDetails(id)

        // --- ASSERT ---
        assertEquals("", result.posterPath)
        assertEquals(23, result.id)
    }
}