package com.example.movieapp.data.local.mapper

import com.example.movieapp.data.local.entity.GenreEntity
import com.example.movieapp.domain.model.genres.Genre

fun GenreEntity.toCacheGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun Genre.toGenreCacheEntity(): GenreEntity {
    return GenreEntity(
        id = id,
        name = name
    )
}