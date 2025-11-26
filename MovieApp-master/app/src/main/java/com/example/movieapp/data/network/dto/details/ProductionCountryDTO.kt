package com.example.movieapp.data.network.dto.details

import com.squareup.moshi.Json

data class ProductionCountryDTO (
    @field:Json(name = "iso_3166_1")
    val iso31661: String?,
    val name: String?
)