package com.example.movieapp.data.network.dto.movies

import com.squareup.moshi.Json

data class MoviesDTO(
    @field:Json(name = "page")
    val page: Int,
    @field:Json(name = "results")
    val results: List<MovieDataDTO>,
    @field:Json(name = "total_pages")
    val totalPages: Int,
    @field:Json(name = "total_results")
    val totalResults: Int
)