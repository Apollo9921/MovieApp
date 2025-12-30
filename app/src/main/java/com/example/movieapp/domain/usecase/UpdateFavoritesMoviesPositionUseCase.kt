package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateFavoritesMoviesPositionUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(newMoviesPosition: List<MovieData>) = flow {
        val updatedPosition = repository.updateMoviePosition(newMoviesPosition)
        emit(Result.success(updatedPosition))
    }.catch {
        emit(Result.failure(it))
    }
}