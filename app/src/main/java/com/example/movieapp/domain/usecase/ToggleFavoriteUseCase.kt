package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ToggleFavoriteUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(movie: MovieData) = flow {
        repository.toggleFavoriteMovie(movie)
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }
}