package com.example.movieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.usecase.GetFavoriteMoviesUseCase
import com.example.movieapp.domain.usecase.UpdateFavoritesMoviesPositionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Collections

class FavoritesViewModel(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val updateFavoritesMoviesPositionUseCase: UpdateFavoritesMoviesPositionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesMoviesUiState())
    val uiState: StateFlow<FavoritesMoviesUiState> = _uiState.asStateFlow()

    data class FavoritesMoviesUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val isError: Boolean = false,
        var errorMessage: String? = null,
        var moviesList: List<MovieData> = emptyList(),
    )

    init {
        getFavoriteMovies()
    }

    fun getFavoriteMovies() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isSuccess = false,
            isError = false,
            errorMessage = null
        )
        viewModelScope.launch {
            getFavoriteMoviesUseCase().collect { result ->
                if (result.isSuccess) {
                    val moviesList = result.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        moviesList = moviesList,
                        isSuccess = true,
                        isLoading = false,
                        isError = false,
                        errorMessage = null
                    )
                } else if (result.isFailure || result.getOrNull().isNullOrEmpty()) {
                    val errorMessage = result.exceptionOrNull()?.message
                    _uiState.value = _uiState.value.copy(
                        isSuccess = false,
                        isLoading = false,
                        isError = true,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    fun moveMovie(from: Int, to: Int) {
        val currentList = _uiState.value.moviesList
        val updatedList = currentList.toMutableList()
        val positionDraggedMovie = currentList[from].voteCount
        val positionMovie = currentList[to].voteCount
        updatedList[from] = updatedList[from].copy(voteCount = positionMovie)
        updatedList[to] = updatedList[to].copy(voteCount = positionDraggedMovie)
        Collections.swap(updatedList, from, to)
        _uiState.value = _uiState.value.copy(
            moviesList = updatedList
        )
    }

    fun updateMoviePosition() {
        viewModelScope.launch {
            updateFavoritesMoviesPositionUseCase(uiState.value.moviesList).first()
        }
    }
}