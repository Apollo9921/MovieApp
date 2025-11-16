package com.example.movieapp.data.network.dto.details

import com.example.movieapp.domain.model.details.ProductionCompany
import com.squareup.moshi.Json

data class ProductionCompanyDTO (
    val id: Int?,
    @field:Json(name = "logo_path")
    val logoPath: String?,
    val name: String?,
    @field:Json(name = "origin_country")
    val originCountry: String?
)

fun ProductionCompanyDTO.toProductionCompany() : ProductionCompany {
    return ProductionCompany(
        id = id ?: 0,
        logoPath = this@toProductionCompany.logoPath ?: "",
        name = name ?: "",
        originCountry = this@toProductionCompany.originCountry ?: ""
    )
}