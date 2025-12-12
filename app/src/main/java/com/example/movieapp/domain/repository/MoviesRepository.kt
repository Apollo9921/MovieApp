package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun fetchMovies(pageNumber: Int): Movies
    suspend fun fetchGenres(): GenresList
    suspend fun searchMovie(query: String): Movies
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun toggleFavoriteMovie(movie: MovieData)
    suspend fun getFavoriteMovies(): Flow<List<MovieData>>
    suspend fun isMovieFavorite(movieId: Int): Flow<Boolean>
}