package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetGenresUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(): Flow<Result<GenresList>> = flow {
        val genres = repository.fetchGenres()
        emit(Result.success(genres))
    }.catch {
        emit(Result.failure(it))
    }
}