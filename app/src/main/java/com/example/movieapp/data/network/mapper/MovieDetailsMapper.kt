package com.example.movieapp.data.network.mapper

import com.example.movieapp.data.network.dto.details.DetailsDTO
import com.example.movieapp.data.network.dto.details.GenreDTO
import com.example.movieapp.data.network.dto.details.ProductionCompanyDTO
import com.example.movieapp.data.network.dto.details.ProductionCountryDTO
import com.example.movieapp.data.network.dto.details.SpokenLanguageDTO
import com.example.movieapp.domain.model.details.Genre
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.details.ProductionCompany
import com.example.movieapp.domain.model.details.ProductionCountry
import com.example.movieapp.domain.model.details.SpokenLanguage

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

fun GenreDTO.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun ProductionCompanyDTO.toProductionCompany() : ProductionCompany {
    return ProductionCompany(
        id = id ?: 0,
        logoPath = this@toProductionCompany.logoPath ?: "",
        name = name ?: "",
        originCountry = this@toProductionCompany.originCountry ?: ""
    )
}

fun ProductionCountryDTO.toProductionCountry(): ProductionCountry {
    return ProductionCountry(
        iso31661 = this@toProductionCountry.iso31661 ?: "",
        name = name ?: ""
    )
}

fun SpokenLanguageDTO.toSpokenLanguage(): SpokenLanguage {
    return SpokenLanguage(
        englishName = this@toSpokenLanguage.englishName ?: "",
        iso6391 = this@toSpokenLanguage.iso6391 ?: "",
        name = name ?: ""
    )
}