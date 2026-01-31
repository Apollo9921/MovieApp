package com.example.movieapp.data.local.mapper

import com.example.movieapp.data.local.entity.MovieEntity
import com.example.movieapp.domain.model.movies.MovieData

fun MovieEntity.toMovieData(): MovieData {
    return MovieData(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        voteAverage = 0.0,
        voteCount = position,
        releaseDate = "",
        genreIds = genreIds,
        popularity = 0.0,
        backdropPath = "",
        originalLanguage = "",
        originalTitle = "",
        adult = false,
        video = false,
        page = 0
    )
}

fun MovieData.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        overview = overview,
        genreIds = genreIds,
        position = voteCount
    )
}