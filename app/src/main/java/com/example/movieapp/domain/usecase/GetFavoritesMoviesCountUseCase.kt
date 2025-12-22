package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetFavoritesMoviesCountUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(): Flow<Result<Int>> = flow {
        val getMovieCount = repository.getMovieCount()
        emit(Result.success(getMovieCount))
    }.catch {
        emit(Result.failure(it))
    }
}