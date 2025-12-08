package com.example.movieapp.data.network.dto.genres

import com.squareup.moshi.Json

data class GenreDTO(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String
)