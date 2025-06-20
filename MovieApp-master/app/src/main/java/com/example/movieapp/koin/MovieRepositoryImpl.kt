package com.example.movieapp.koin

import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.Movies
import com.example.movieapp.networking.requests.MovieService
import retrofit2.Response

class MovieRepositoryImpl(
    private val movieService: MovieService
): MoviesRepository {
    override suspend fun fetchMovies(pageNumber: Int): Response<Movies> {
        return movieService.getMovies(pageNumber)
    }

    override suspend fun fetchGenres(): Response<GenresList> {
        return movieService.getGenres()
    }

    override suspend fun searchMovie(query: String): Response<Movies> {
        return movieService.searchMovie(query)
    }
}