package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetMovieDetailsUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(movieId: Int): Flow<Result<MovieDetails>> = flow {
        val moviesDetails = repository.getMovieDetails(movieId)
        emit(Result.success(moviesDetails))
    }.catch {
        emit(Result.failure(it))
    }
}