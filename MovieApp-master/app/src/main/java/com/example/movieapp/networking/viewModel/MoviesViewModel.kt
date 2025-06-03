package com.example.movieapp.networking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.networking.instance.MovieInstance
import com.example.movieapp.networking.model.Movies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel : ViewModel() {

    private val _moviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val moviesState: StateFlow<MoviesState> = _moviesState.asStateFlow()

    private val apiService = MovieInstance.api

    sealed class MoviesState {
        data object Loading : MoviesState()
        data class Success(val movies: Movies) : MoviesState()
        data class Error(val message: String) : MoviesState()
    }

    fun fetchMovies() {
        _moviesState.value = MoviesState.Loading
        viewModelScope.launch {
            try {
                val response = apiService.getMovies()
                if (response.isSuccessful && response.body() != null) {
                    _moviesState.value = MoviesState.Success(response.body()!!)
                } else {
                    _moviesState.value = MoviesState.Error("Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error("Exception: ${e.message ?: "Unknown error"}")
            }
        }
    }
}