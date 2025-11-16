package com.example.movieapp.data.network.dto.details

import com.example.movieapp.domain.model.details.ProductionCountry
import com.squareup.moshi.Json

data class ProductionCountryDTO (
    @field:Json(name = "iso_3166_1")
    val iso31661: String?,
    val name: String?
)

fun ProductionCountryDTO.toProductionCountry(): ProductionCountry {
    return ProductionCountry(
        iso31661 = this@toProductionCountry.iso31661 ?: "",
        name = name ?: ""
    )
}