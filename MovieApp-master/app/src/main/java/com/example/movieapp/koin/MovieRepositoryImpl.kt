package com.example.movieapp.koin

import com.example.movieapp.networking.model.details.MovieDetails
import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.Movies
import com.example.movieapp.networking.requests.MovieService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class MovieRepositoryImpl(
    private val movieService: MovieService
) : MoviesRepository {
    override suspend fun fetchMovies(pageNumber: Int): Response<Movies> {
        return withContext(Dispatchers.IO) {
            movieService.getMovies(pageNumber)
        }
    }

    override suspend fun fetchGenres(): Response<GenresList> {
        return withContext(Dispatchers.IO) {
            movieService.getGenres()
        }
    }

    override suspend fun searchMovie(query: String): Response<Movies> {
        return withContext(Dispatchers.IO) {
            movieService.searchMovie(query)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Response<MovieDetails> {
        return withContext(Dispatchers.IO) {
            movieService.getMovieDetails(movieId)
        }
    }
}