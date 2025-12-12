package com.example.movieapp.data.local.mapper

import com.example.movieapp.data.local.entity.MovieEntity
import com.example.movieapp.domain.model.movies.MovieData

fun MovieEntity.toMovieData(): MovieData {
    return MovieData(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        voteAverage = voteAverage.toDouble(),
        voteCount = voteCount.toInt(),
        releaseDate = releaseDate,
        genreIds = emptyList(),
        popularity = 0.0,
        backdropPath = "",
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        adult = false,
        video = false
    )
}

fun MovieData.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate,
        genreIds = 0,
        overview = overview,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        adult = adult,
        video = video,
        popularity = popularity,
        backdropPath = backdropPath
    )
}