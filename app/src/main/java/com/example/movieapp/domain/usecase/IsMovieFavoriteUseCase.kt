package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class IsMovieFavoriteUseCase (
    private val repository: MoviesRepository
) {
    operator fun invoke(movieId: Int) : Flow<Result<Boolean>> = flow {
        val isFavorite = repository.isMovieFavorite(movieId)
        emit(Result.success(isFavorite))
    }.catch {
        emit(Result.failure(it))
    }

}