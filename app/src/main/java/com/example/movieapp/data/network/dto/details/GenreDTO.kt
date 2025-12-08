package com.example.movieapp.data.network.dto.details

import com.squareup.moshi.Json

data class GenreDTO (
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "name")
    val name: String
)