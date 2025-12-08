package com.example.movieapp.data.repository

import com.example.movieapp.data.network.mapper.toGenresList
import com.example.movieapp.data.network.mapper.toMovieDetails
import com.example.movieapp.data.network.mapper.toMovies
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.data.network.service.MovieService
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val movieService: MovieService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MoviesRepository {
    override suspend fun fetchMovies(pageNumber: Int): Movies {
        return withContext(ioDispatcher) {
            movieService.getMovies(pageNumber).toMovies()
        }
    }

    override suspend fun fetchGenres(): GenresList {
        return withContext(ioDispatcher) {
            movieService.getGenres().toGenresList()
        }
    }

    override suspend fun searchMovie(query: String): Movies {
        return withContext(ioDispatcher) {
            movieService.searchMovie(query).toMovies()
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return withContext(ioDispatcher) {
            movieService.getMovieDetails(movieId).toMovieDetails()
        }
    }
}