package com.example.movieapp.data.network.mapper

import com.example.movieapp.data.network.dto.movies.MovieDataDTO
import com.example.movieapp.data.network.dto.movies.MoviesDTO
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies

fun MoviesDTO.toMovies(): Movies {
    return Movies(
        page = page,
        results = results.map { it.toMovieData() },
        totalPages = totalPages,
        totalResults = totalResults
    )
}

fun MovieDataDTO.toMovieData(): MovieData {
    return MovieData(
        adult = adult == true,
        backdropPath = backdropPath ?: "",
        genreIds = genreIds ?: emptyList(),
        id = id ?: 0,
        originalLanguage = originalLanguage ?: "",
        originalTitle = originalTitle ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = posterPath ?: "",
        releaseDate = releaseDate ?: "",
        title = title ?: "",
        video = video == true,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        page = 0
    )
}