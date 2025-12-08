package com.example.movieapp.data.network.mapper

import com.example.movieapp.data.network.dto.movies.MovieDataDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MoviesMapperTest {

    @Test
    fun `toMovieData should map correctly when all fields are present`() {
        // --- ARRANGE ---
        val dto = MovieDataDTO(
            id = 1,
            title = "Dune",
            adult = true,
            backdropPath = "/path.jpg",
            genreIds = listOf(1, 2),
            originalLanguage = "en",
            originalTitle = "Dune",
            overview = "Sand...",
            popularity = 10.0,
            posterPath = "/poster.jpg",
            releaseDate = "2023-01-01",
            video = true,
            voteAverage = 9.5,
            voteCount = 100
        )

        // --- ACT ---
        val domain = dto.toMovieData()

        // --- ASSERT ---
        assertEquals(1, domain.id)
        assertEquals("Dune", domain.title)
        assertEquals(true, domain.adult)
        assertEquals("/path.jpg", domain.backdropPath)
    }

    @Test
    fun `toMovieData should handle NULL values with safe defaults`() {
        // --- ARRANGE ---
        val nullDto = MovieDataDTO(
            id = null,
            title = null,
            adult = null,
            backdropPath = null,
            genreIds = null,
            originalLanguage = null,
            originalTitle = null,
            overview = null,
            popularity = null,
            posterPath = null,
            releaseDate = null,
            video = null,
            voteAverage = null,
            voteCount = null
        )

        // --- ACT ---
        val domain = nullDto.toMovieData()

        // --- ASSERT ---
        assertEquals(0, domain.id)
        assertEquals("", domain.title)
        assertFalse(domain.adult)
        assertEquals(emptyList<Int>(), domain.genreIds)
    }
}