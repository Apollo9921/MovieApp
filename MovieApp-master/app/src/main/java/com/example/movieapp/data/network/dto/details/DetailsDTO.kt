package com.example.movieapp.data.network.dto.details

import com.example.movieapp.domain.model.details.MovieDetails
import com.squareup.moshi.Json

data class DetailsDTO (
    @field:Json(name = "adult")
    val adult: Boolean?,
    @field:Json(name = "backdrop_path")
    val backdropPath: String?,
    @field:Json(name = "belongs_to_collection")
    val belongsToCollection: Any?,
    @field:Json(name = "budget")
    val budget: Int?,
    @field:Json(name = "genres")
    val genres: List<GenreDTO>?,
    @field:Json(name = "homepage")
    val homepage: String?,
    @field:Json(name = "id")
    val id: Int?,
    @field:Json(name = "imdb_id")
    val imdbId: String?,
    @field:Json(name = "origin_country")
    val originCountry: List<String>?,
    @field:Json(name = "original_language")
    val originalLanguage: String?,
    @field:Json(name = "original_title")
    val originalTitle: String?,
    @field:Json(name = "overview")
    val overview: String?,
    @field:Json(name = "popularity")
    val popularity: Double?,
    @field:Json(name = "poster_path")
    val posterPath: String?,
    @field:Json(name = "production_companies")
    val productionCompanies: List<ProductionCompanyDTO>?,
    @field:Json(name = "production_countries")
    val productionCountries: List<ProductionCountryDTO>?,
    @field:Json(name = "release_date")
    val releaseDate: String?,
    @field:Json(name = "revenue")
    val revenue: Long?,
    @field:Json(name = "runtime")
    val runtime: Int?,
    @field:Json(name = "spoken_languages")
    val spokenLanguages: List<SpokenLanguageDTO>?,
    @field:Json(name = "status")
    val status: String?,
    @field:Json(name = "tagline")
    val tagline: String?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "video")
    val video: Boolean?,
    @field:Json(name = "vote_average")
    val voteAverage: Double?,
    @field:Json(name = "vote_count")
    val voteCount: Int?
)

fun DetailsDTO.toMovieDetails() : MovieDetails {
    return MovieDetails(
        adult = adult == true,
        backdropPath = this@toMovieDetails.backdropPath ?: "",
        belongsToCollection = this@toMovieDetails.belongsToCollection ?: "",
        budget = budget ?: 0,
        genres = genres?.map { it.toGenre() } ?: emptyList(),
        homepage = homepage ?: "",
        id = id ?: 0,
        imdbId = this@toMovieDetails.imdbId ?: "",
        originCountry = this@toMovieDetails.originCountry ?: emptyList(),
        originalLanguage = this@toMovieDetails.originalLanguage ?: "",
        originalTitle = this@toMovieDetails.originalTitle ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = this@toMovieDetails.posterPath ?: "",
        productionCompanies = this@toMovieDetails.productionCompanies?.map { it.toProductionCompany() } ?: emptyList(),
        productionCountries = this@toMovieDetails.productionCountries?.map { it.toProductionCountry() } ?: emptyList(),
        releaseDate = this@toMovieDetails.releaseDate ?: "",
        revenue = revenue ?: 0L,
        runtime = runtime ?: 0,
        spokenLanguages = this@toMovieDetails.spokenLanguages?.map { it.toSpokenLanguage() } ?: emptyList(),
        status = status ?: "",
        tagline = tagline ?: "",
        title = title ?: "",
        video = video == true,
        voteAverage = this@toMovieDetails.voteAverage ?: 0.0,
        voteCount = this@toMovieDetails.voteCount ?: 0
    )
}