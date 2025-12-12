package com.example.movieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.usecase.AddFavoritesUseCase
import com.example.movieapp.domain.usecase.GetFavoriteMoviesUseCase
import com.example.movieapp.domain.usecase.IsMovieFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val addFavoritesUseCase: AddFavoritesUseCase,
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesMoviesUiState())
    val uiState: StateFlow<FavoritesMoviesUiState> = _uiState.asStateFlow()

    data class FavoritesMoviesUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val isError: Boolean = false,
        var errorMessage: String? = null,
        var moviesList: List<MovieData> = emptyList(),
        var query: String = ""
    )

    fun toggleMovie(movie: MovieData) {
        viewModelScope.launch {
            addFavoritesUseCase(movie)
        }
    }

    fun getFavoriteMovies() {
        viewModelScope.launch {
            getFavoriteMoviesUseCase().collect { result ->
                val moviesList = result.getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    moviesList = moviesList,
                    isSuccess = true,
                    isLoading = false,
                    isError = false,
                    errorMessage = null
                )
            }
        }
    }
}