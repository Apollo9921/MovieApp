package com.example.movieapp.networking.model.movies

data class Movies(
    val page: Int,
    val results: List<MovieData>,
    val total_pages: Int,
    val total_results: Int
)