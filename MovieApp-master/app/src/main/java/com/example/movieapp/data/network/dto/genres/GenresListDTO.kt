package com.example.movieapp.data.network.dto.genres

import com.example.movieapp.domain.model.genres.GenresList

data class GenresListDTO (
    val genres: List<GenreDTO>
)

fun GenresListDTO.toGenresList(): GenresList {
    return GenresList(
        genres = genres.map { it.toGenre() }
    )
}