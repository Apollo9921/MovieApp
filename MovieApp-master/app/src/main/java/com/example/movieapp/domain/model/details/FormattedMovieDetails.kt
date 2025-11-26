package com.example.movieapp.domain.model.details

data class FormattedMovieDetails(
    val title: String,
    val overview: String,
    val posterUrl: String,
    val voteAverage: String,
    val voteCount: String,
    val releaseYear: String,
    val genres: String,
    val runtime: String,
    val spokenLanguages: List<String>,
    val productionCompanies: List<String>
)