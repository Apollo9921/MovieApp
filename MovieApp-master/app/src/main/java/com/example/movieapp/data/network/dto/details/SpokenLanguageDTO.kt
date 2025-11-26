package com.example.movieapp.data.network.dto.details

import com.squareup.moshi.Json

data class SpokenLanguageDTO(
    @field:Json(name = "english_name")
    val englishName: String?,
    @field:Json(name = "iso_639_1")
    val iso6391: String?,
    val name: String?
)