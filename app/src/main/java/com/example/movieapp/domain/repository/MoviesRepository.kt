package com.example.movieapp.domain.repository

import com.example.movieapp.data.local.entity.GenreEntity
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies

interface MoviesRepository {
    suspend fun fetchMovies(pageNumber: Int, moviesList: List<MovieData>): Movies
    suspend fun fetchGenres(): GenresList
    suspend fun searchMovie(query: String): Movies
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun toggleFavoriteMovie(movie: MovieData, isFavorite: Boolean)
    suspend fun getFavoriteMovies(): List<MovieData>
    suspend fun isMovieFavorite(movieId: Int): Boolean
    suspend fun updateMoviePosition(newMoviesPosition: List<MovieData>)
    suspend fun getMovieCount(): Int
    suspend fun insertMoviesCache(movies: List<MovieData>, pageNumber: Int)
    suspend fun getMoviesCache(): List<MovieData>
    suspend fun insertGenresCache(genres: List<Genre>)
    suspend fun getGenresCache(): List<GenreEntity>
    suspend fun clearMoviesCache()
}