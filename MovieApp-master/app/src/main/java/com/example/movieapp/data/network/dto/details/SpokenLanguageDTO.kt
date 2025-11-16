package com.example.movieapp.data.network.dto.details

import com.example.movieapp.domain.model.details.SpokenLanguage
import com.squareup.moshi.Json

data class SpokenLanguageDTO(
    @field:Json(name = "english_name")
    val englishName: String?,
    @field:Json(name = "iso_639_1")
    val iso6391: String?,
    val name: String?
)

fun SpokenLanguageDTO.toSpokenLanguage(): SpokenLanguage {
    return SpokenLanguage(
        englishName = this@toSpokenLanguage.englishName ?: "",
        iso6391 = this@toSpokenLanguage.iso6391 ?: "",
        name = name ?: ""
    )
}