package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.MovieDao
import com.example.movieapp.data.local.entity.GenreEntity
import com.example.movieapp.data.local.mapper.toCacheGenre
import com.example.movieapp.data.local.mapper.toGenreCacheEntity
import com.example.movieapp.data.local.mapper.toMovieCacheData
import com.example.movieapp.data.local.mapper.toMovieCacheEntity
import com.example.movieapp.data.local.mapper.toMovieData
import com.example.movieapp.data.local.mapper.toMovieEntity
import com.example.movieapp.data.network.mapper.toGenresList
import com.example.movieapp.data.network.mapper.toMovieDetails
import com.example.movieapp.data.network.mapper.toMovies
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.data.network.service.MovieService
import com.example.movieapp.domain.model.genres.Genre
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
    override suspend fun fetchMovies(pageNumber: Int, moviesList: List<MovieData>): Movies {
        return withContext(ioDispatcher) {
            try {
                val apiResponse = movieService.getMovies(pageNumber).toMovies()
                if (pageNumber == 1) movieDao.clearMoviesCache()
                insertMoviesCache(apiResponse.results, pageNumber)
                apiResponse
            } catch (e: Exception) {
                val cached = getMoviesCache()
                if (cached.isNotEmpty() && moviesList.isEmpty()) {
                    Movies(page = cached.last().page, results = cached, totalPages = 1, totalResults = cached.size)
                } else throw e
            }
        }
    }

    override suspend fun fetchGenres(): GenresList {
        return withContext(ioDispatcher) {
            try {
                val apiGenres = movieService.getGenres().toGenresList()
                insertGenresCache(apiGenres.genres)
                apiGenres
            } catch (e: Exception) {
                val cached = getGenresCache()
                if (cached.isNotEmpty()) {
                    GenresList(cached.map { it.toCacheGenre() })
                } else throw e
            }
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

    override suspend fun toggleFavoriteMovie(movie: MovieData, isFavorite: Boolean) {
        withContext(ioDispatcher) {
            if (isFavorite) {
                movieDao.deleteMovie(movie.toMovieEntity())
                val remainingMovies = getFavoriteMovies()
                val updatedMoviesWithNewPositions = remainingMovies.mapIndexed { index, movieData ->
                    movieData.copy(voteCount = index)
                }
                if (updatedMoviesWithNewPositions.isNotEmpty()) {
                    updateMoviePosition(updatedMoviesWithNewPositions)
                }
            } else {
                val currentMovies = getFavoriteMovies()
                val updatedMovies = currentMovies.map { it.copy(voteCount = it.voteCount + 1) }
                updateMoviePosition(updatedMovies)
                movieDao.insertMovie(movie.toMovieEntity())
            }
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
        return movieDao.getMovieCount()
    }

    override suspend fun insertMoviesCache(movies: List<MovieData>, pageNumber: Int) {
        val cacheEntities = movies.map { it.toMovieCacheEntity(pageNumber) }
        movieDao.insertMoviesCache(cacheEntities)
    }

    override suspend fun getMoviesCache(): List<MovieData> {
        return movieDao.getMoviesCache().map { it.toMovieCacheData() }
    }

    override suspend fun insertGenresCache(genres: List<Genre>) {
       return movieDao.insertGenres(genres.map { it.toGenreCacheEntity() })
    }

    override suspend fun getGenresCache(): List<GenreEntity> {
        return movieDao.getAllGenres()
    }

    override suspend fun clearMoviesCache() {
        return movieDao.clearMoviesCache()
    }
}