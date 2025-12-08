package com.example.movieapp.data.network.mapper

import com.example.movieapp.data.network.dto.genres.GenreDTO
import com.example.movieapp.data.network.dto.genres.GenresListDTO
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList

fun GenresListDTO.toGenresList(): GenresList {
    return GenresList(
        genres = genres.map { it.toGenre() }
    )
}

fun GenreDTO.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}