package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.Movies
import retrofit2.Response

interface MoviesRepository {
    suspend fun fetchMovies(pageNumber: Int): Movies
    suspend fun fetchGenres(): GenresList
    suspend fun searchMovie(query: String): Response<Movies>
    suspend fun getMovieDetails(movieId: Int): Response<MovieDetails>
}