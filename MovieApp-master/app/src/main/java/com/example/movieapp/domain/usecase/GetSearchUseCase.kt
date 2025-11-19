package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetSearchUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(query: String): Flow<Result<Movies>> = flow {
        val movies = repository.searchMovie(query)
        emit(Result.success(movies))
    }.catch {
        emit(Result.failure(it))
    }
}