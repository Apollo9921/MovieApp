package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetMoviesUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(pageNumber: Int, moviesList: List<MovieData>): Flow<Result<Movies>> = flow {
        val movies = repository.fetchMovies(pageNumber, moviesList)
        emit(Result.success(movies))
    }.catch {
        emit(Result.failure(it))
    }
}
