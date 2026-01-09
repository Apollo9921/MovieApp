package com.example.movieapp.data.local.mapper

import com.example.movieapp.data.local.entity.MovieCacheEntity
import com.example.movieapp.domain.model.movies.MovieData

fun MovieCacheEntity.toMovieCacheData(): MovieData {
    return MovieData(
        id = id,
        title = title,
        overview = "",
        posterPath = posterPath,
        voteAverage = 0.0,
        voteCount = 0,
        releaseDate = "",
        genreIds = genreIds,
        popularity = 0.0,
        backdropPath = "",
        originalLanguage = "",
        originalTitle = "",
        adult = false,
        video = false,
        page = page
    )
}

fun MovieData.toMovieCacheEntity(page: Int): MovieCacheEntity {
    return MovieCacheEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        genreIds = genreIds,
        page = page
    )
}