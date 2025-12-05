package com.example.movieapp.data.network.mapper

import com.example.movieapp.data.network.dto.details.DetailsDTO
import com.example.movieapp.data.network.dto.details.GenreDTO
import com.example.movieapp.data.network.dto.details.ProductionCompanyDTO
import com.example.movieapp.data.network.dto.details.ProductionCountryDTO
import com.example.movieapp.data.network.dto.details.SpokenLanguageDTO
import com.example.movieapp.domain.model.details.ProductionCompany
import com.example.movieapp.domain.model.details.ProductionCountry
import com.example.movieapp.domain.model.details.SpokenLanguage
import com.example.movieapp.domain.model.genres.Genre
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MovieDetailsMapperTest {

    private val genreDto = listOf(
        GenreDTO(
            id = 1,
            name = "Action"
        ),
        GenreDTO(
            id = 2,
            name = "Adventure"
        )
    )

    private val productionCompanyDto = listOf(
        ProductionCompanyDTO(
            id = 1,
            logoPath = "/path.jpg",
            name = "Company",
            originCountry = "US"
        )
    )

    private val spokenLanguageDto = listOf(
        SpokenLanguageDTO(
            englishName = "English",
            iso6391 = "en",
            name = "English"
        )
    )

    private val productionCountryDto = listOf(
        ProductionCountryDTO(
            iso31661 = "US",
            name = "United States"
        )
    )

    @Test
    fun `toMovieDetails should map correctly when all fields are present`() {
        // --- ARRANGE ---
        val dto = DetailsDTO(
            id = 1,
            title = "Dune",
            adult = true,
            backdropPath = "/path.jpg",
            genres = genreDto,
            originalLanguage = "en",
            originalTitle = "Dune",
            overview = "Sand...",
            popularity = 10.0,
            posterPath = "/poster.jpg",
            releaseDate = "2023-01-01",
            video = true,
            voteAverage = 9.5,
            voteCount = 100,
            budget = 1000000,
            homepage = "https://example.com",
            imdbId = "imdb123",
            productionCompanies = productionCompanyDto,
            productionCountries = productionCountryDto,
            revenue = 2000000,
            runtime = 150,
            spokenLanguages = spokenLanguageDto,
            status = "Released",
            tagline = "A movie about...",
            originCountry = listOf(""),
            belongsToCollection = "Collection"
        )

        // --- ACT ---
        val domain = dto.toMovieDetails()

        // --- ASSERT ---
        assertEquals(1, domain.id)
        assertEquals("Dune", domain.title)
        assertEquals(true, domain.adult)
        assertEquals("/path.jpg", domain.backdropPath)
        assertEquals(1000000, domain.budget)
        assertEquals("https://example.com", domain.homepage)
        assertEquals("imdb123", domain.imdbId)
        assertEquals(2, domain.genres.size)
        assertEquals("Action", domain.genres[0].name)
        assertEquals("Adventure", domain.genres[1].name)
        assertEquals(1, domain.productionCompanies.size)
        assertEquals("/path.jpg", domain.productionCompanies[0].logoPath)
        assertEquals("Company", domain.productionCompanies[0].name)
        assertEquals("US", domain.productionCompanies[0].originCountry)
        assertEquals(1, domain.productionCountries.size)
        assertEquals("US", domain.productionCountries[0].iso31661)
        assertEquals("United States", domain.productionCountries[0].name)
        assertEquals("2023-01-01", domain.releaseDate)
        assertEquals(2000000, domain.revenue)
        assertEquals(150, domain.runtime)
        assertEquals(1, domain.spokenLanguages.size)
        assertEquals("English", domain.spokenLanguages[0].englishName)
        assertEquals("en", domain.spokenLanguages[0].iso6391)
        assertEquals("English", domain.spokenLanguages[0].name)
        assertEquals("Released", domain.status)
        assertEquals("A movie about...", domain.tagline)
        assertEquals("Collection", domain.belongsToCollection)
        assertEquals(100, domain.voteCount)
        assertEquals(9.5, domain.voteAverage)
    }

    @Test
    fun `toMovieDetails should handle NULL values with safe defaults`() = runTest {
        // --- ARRANGE ---
        val dto = DetailsDTO(
            id = null,
            title = null,
            adult = null,
            backdropPath = null,
            genres = null,
            originalLanguage = null,
            originalTitle = null,
            overview = null,
            popularity = null,
            posterPath = null,
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

        // --- ACT ---
        val domain = dto.toMovieDetails()

        // --- ASSERT ---
        assertEquals(0, domain.id)
        assertEquals("", domain.title)
        assertEquals(false, domain.adult)
        assertEquals("", domain.backdropPath)
        assertEquals(0, domain.budget)
        assertEquals("", domain.homepage)
        assertEquals("", domain.imdbId)
        assertEquals(emptyList<Genre>(), domain.genres)
        assertEquals("", domain.originalLanguage)
        assertEquals("", domain.originalTitle)
        assertEquals("", domain.overview)
        assertEquals(0.0, domain.popularity)
        assertEquals("", domain.posterPath)
        assertEquals("", domain.releaseDate)
        assertEquals(false, domain.video)
        assertEquals(0.0, domain.voteAverage)
        assertEquals(0, domain.voteCount)
        assertEquals(emptyList<ProductionCompany>(), domain.productionCompanies)
        assertEquals(emptyList<ProductionCountry>(), domain.productionCountries)
        assertEquals(0L, domain.revenue)
        assertEquals(0, domain.runtime)
        assertEquals(emptyList<SpokenLanguage>(), domain.spokenLanguages)
        assertEquals("", domain.status)
        assertEquals("", domain.tagline)
        assertEquals("", domain.belongsToCollection)
        assertEquals(emptyList<String>(), domain.originCountry)
    }

}