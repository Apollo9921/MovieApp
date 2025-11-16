package com.example.movieapp.data.repository

import com.example.movieapp.data.network.dto.details.toMovieDetails
import com.example.movieapp.data.network.dto.genres.toGenresList
import com.example.movieapp.data.network.dto.movies.toMovies
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.data.network.service.MovieService
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val movieService: MovieService
) : MoviesRepository {
    override suspend fun fetchMovies(pageNumber: Int): Movies {
        return withContext(Dispatchers.IO) {
            movieService.getMovies(pageNumber).toMovies()
        }
    }

    override suspend fun fetchGenres(): GenresList {
        return withContext(Dispatchers.IO) {
            movieService.getGenres().toGenresList()
        }
    }

    override suspend fun searchMovie(query: String): Movies {
        return withContext(Dispatchers.IO) {
            movieService.searchMovie(query).toMovies()
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return withContext(Dispatchers.IO) {
            movieService.getMovieDetails(movieId).toMovieDetails()
        }
    }
}