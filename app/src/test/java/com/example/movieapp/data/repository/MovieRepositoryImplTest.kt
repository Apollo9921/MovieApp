package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.MovieDao
import com.example.movieapp.data.local.entity.MovieEntity
import com.example.movieapp.data.local.mapper.toMovieEntity
import com.example.movieapp.data.network.dto.details.DetailsDTO
import com.example.movieapp.data.network.dto.genres.GenreDTO
import com.example.movieapp.data.network.dto.genres.GenresListDTO
import com.example.movieapp.data.network.dto.movies.MovieDataDTO
import com.example.movieapp.data.network.dto.movies.MoviesDTO
import com.example.movieapp.data.network.service.MovieService
import com.example.movieapp.domain.model.movies.MovieData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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

    private val fakeMovie = MovieData(
        id = 101,
        title = "Test Movie",
        posterPath = "",
        adult = false,
        backdropPath = "",
        genreIds = emptyList(),
        originalLanguage = "",
        originalTitle = "",
        overview = "",
        popularity = 0.0,
        releaseDate = "",
        video = false,
        voteAverage = 0.0,
        voteCount = 0
    )

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

    @Test
    fun `getMovieCount should return the correct count`() = runTest {
        // --- ARRANGE ---
        val fakeCount = 10
        coEvery { movieDao.getMovieCount() } returns fakeCount

        // --- ACT ---
        val result = repository.getMovieCount()

        // --- ASSERT ---
        assertEquals(fakeCount, result)
    }

    @Test
    fun `insertMovie should insert the movie into the database`() = runTest {
        // --- ARRANGE ---
        coEvery { movieDao.insertMovie(fakeMovie.toMovieEntity()) } returns Unit
        coEvery { movieDao.getFavoriteMovies() } returns listOf(fakeMovie.toMovieEntity())
        coEvery { movieDao.updateMoviePosition(any()) } returns Unit

        // --- ACT ---
        repository.toggleFavoriteMovie(fakeMovie, false)
        val result = repository.getFavoriteMovies()

        // --- ASSERT ---
        assertEquals(1, result.size)
        assertEquals(101, result[0].id)
        assertEquals("Test Movie", result[0].title)
    }

    @Test
    fun `deleteMovie should delete the movie from the database`() = runTest {
        // --- ARRANGE ---
        coEvery { movieDao.deleteMovie(fakeMovie.toMovieEntity()) } returns Unit
        coEvery { movieDao.getFavoriteMovies() } returns emptyList()

        // --- ACT ---
        repository.toggleFavoriteMovie(fakeMovie, true)
        val result = repository.getFavoriteMovies()

        // --- ASSERT ---
        assertEquals(0, result.size)
    }

    @Test
    fun `getFavoriteMovies should return the correct list of movies`() = runTest {
        // --- ARRANGE ---
        val fakeMovies = listOf(fakeMovie)
        coEvery { movieDao.getFavoriteMovies() } returns fakeMovies.map { it.toMovieEntity() }

        // --- ACT ---
        val result = repository.getFavoriteMovies()

        // --- ASSERT ---
        assertEquals(1, result.size)
        assertEquals(101, result[0].id)
    }

    @Test
    fun `isMovieFavorite should return true value`() = runTest {
        // --- ARRANGE ---
        val movieId = 10
        coEvery { movieDao.isMovieFavorite(movieId) } returns true

        // --- ACT ---
        val result = repository.isMovieFavorite(movieId)

        // --- ASSERT ---
        assertEquals(true, result)
    }

    @Test
    fun `isMovieFavorite should return false value`() = runTest {
        // --- ARRANGE ---
        val movieId = 10
        coEvery { movieDao.isMovieFavorite(movieId) } returns false

        // --- ACT ---
        val result = repository.isMovieFavorite(movieId)

        // --- ASSERT ---
        assertEquals(false, result)
    }

    @Test
    fun `updateMoviePosition should call the correctly ordered entities`() = runTest {
        // --- ARRANGE ---
        val initialMovies = listOf(
            fakeMovie,
            fakeMovie.copy(id = 102, title = "Test Movie 2"),
            fakeMovie.copy(id = 103, title = "Test Movie 3")
        )
        val shuffledMovies = initialMovies.shuffled()

        val entityListSlot = slot<List<MovieEntity>>()
        coEvery { movieDao.updateMoviePosition(capture(entityListSlot)) } returns Unit

        // --- ACT ---
        repository.updateMoviePosition(shuffledMovies)

        // --- ASSERT ---
        coVerify(exactly = 1) { movieDao.updateMoviePosition(any()) }

        val capturedList = entityListSlot.captured
        println("Current list:")
        capturedList.forEach {
            println("ID: ${it.id}, Position: ${it.title}")
        }

        println("Shuffled list:")
        shuffledMovies.forEach {
            println("ID: ${it.id}, Title: ${it.title}")
        }

        assertEquals(3, capturedList.size)

        assertEquals(shuffledMovies[0].id, capturedList[0].id)
        assertEquals(shuffledMovies[0].title, capturedList[0].title)

        assertEquals(shuffledMovies[1].id, capturedList[1].id)
        assertEquals(shuffledMovies[1].title, capturedList[1].title)

        assertEquals(shuffledMovies[2].id, capturedList[2].id)
        assertEquals(shuffledMovies[2].title, capturedList[2].title)
    }
}