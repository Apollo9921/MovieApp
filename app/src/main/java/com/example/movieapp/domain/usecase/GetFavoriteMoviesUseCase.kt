package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetFavoriteMoviesUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke() : Flow<Result<List<MovieData>>> = flow {
        val favoriteMovies = repository.getFavoriteMovies()
        emit(Result.success(favoriteMovies))
    }.catch {
        emit(Result.failure(it))
    }
}