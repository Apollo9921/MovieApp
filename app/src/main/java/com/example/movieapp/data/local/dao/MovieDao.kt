package com.example.movieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.movieapp.data.local.entity.GenreEntity
import com.example.movieapp.data.local.entity.MovieCacheEntity
import com.example.movieapp.data.local.entity.MovieEntity

@Dao
interface MovieDao {

    @Query("SELECT COUNT(id) FROM movies_table")
    suspend fun getMovieCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies_table ORDER BY position ASC")
    suspend fun getFavoriteMovies(): List<MovieEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM movies_table WHERE id = :movieId)")
    suspend fun isMovieFavorite(movieId: Int): Boolean

    @Update
    suspend fun updateMoviePosition(newMoviesPosition: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoviesCache(movies: List<MovieCacheEntity>)

    @Query("SELECT * FROM movies_cache_table ORDER BY page ASC")
    suspend fun getMoviesCache(): List<MovieCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("SELECT * FROM genres_table")
    suspend fun getAllGenres(): List<GenreEntity>

    @Query("DELETE FROM movies_cache_table")
    suspend fun clearMoviesCache()
}