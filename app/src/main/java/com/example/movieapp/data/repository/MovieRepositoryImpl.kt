package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.MovieDao
import com.example.movieapp.data.local.mapper.toMovieData
import com.example.movieapp.data.local.mapper.toMovieEntity
import com.example.movieapp.data.network.mapper.toGenresList
import com.example.movieapp.data.network.mapper.toMovieDetails
import com.example.movieapp.data.network.mapper.toMovies
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.data.network.service.MovieService
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val movieService: MovieService,
    private val movieDao: MovieDao,
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

    override suspend fun toggleFavoriteMovie(movie: MovieData) {
        val isMovieFavorite = isMovieFavorite(movie.id)
        withContext(ioDispatcher) {
            if (isMovieFavorite) {
                movieDao.deleteMovie(movie.toMovieEntity())
                return@withContext
            }
            val currentMovies = getFavoriteMovies()
            val updatedMovies = currentMovies.map { it.copy(voteCount = it.voteCount + 1) }
            updateMoviePosition(updatedMovies)
            movieDao.insertMovie(movie.toMovieEntity())
        }
    }

    override suspend fun getFavoriteMovies(): List<MovieData> {
        return movieDao.getFavoriteMovies().map { entitiesList ->
            entitiesList.toMovieData()
        }
    }

    override suspend fun isMovieFavorite(movieId: Int): Boolean {
        return movieDao.isMovieFavorite(movieId)
    }

    override suspend fun updateMoviePosition(newMoviesPosition: List<MovieData>) {
        return movieDao.updateMoviePosition(newMoviesPosition.map { it.toMovieEntity() })
    }

    override suspend fun getMovieCount(): Int {
        return  movieDao.getMovieCount()
    }
}