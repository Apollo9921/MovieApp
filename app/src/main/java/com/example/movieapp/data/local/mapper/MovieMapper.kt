package com.example.movieapp.data.local.mapper

import com.example.movieapp.data.local.entity.MovieEntity
import com.example.movieapp.domain.model.movies.MovieData

fun MovieEntity.toMovieData(): MovieData {
    return MovieData(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate,
        overview = overview,
        popularity = popularity,
        backdropPath = backdropPath,
        genreIds = emptyList(),
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        adult = adult,
        video = video
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
        overview = overview,
        popularity = popularity,
        backdropPath = backdropPath,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        adult = adult,
        video = video
    )
}