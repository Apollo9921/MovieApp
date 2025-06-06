package com.example.movieapp.koin

import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.Movies
import retrofit2.Response

interface MoviesRepository {
    suspend fun fetchMovies(pageNumber: Int): Response<Movies>
    suspend fun fetchGenres(): Response<GenresList>
}