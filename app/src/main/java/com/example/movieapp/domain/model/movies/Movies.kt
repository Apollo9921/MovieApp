package com.example.movieapp.domain.model.movies

data class Movies(
    val page: Int,
    val results: List<MovieData>,
    val totalPages: Int,
    val totalResults: Int
)