package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.details.FormattedMovieDetails
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.FormatMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val formatMovieDetailsUseCase: FormatMovieDetailsUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    data class MovieDetailsUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: Boolean = false,
        var errorMessage: String? = null,
        var movieDetails: FormattedMovieDetails? = null,
        var movieId: Int = 0
    )

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    init {
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status == ConnectivityObserver.Status.Available && uiState.value.movieDetails == null) {
                    fetchMovieDetails(uiState.value.movieId)
                } else if (status == ConnectivityObserver.Status.Unavailable) {
                    _uiState.value = _uiState.value.copy(
                        error = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION
                    )
                }
            }
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = false)
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _uiState.value = _uiState.value.copy(
                        error = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION
                    )
                    return@launch
                }
                val response = getMovieDetailsUseCase(movieId).first()
                val movieDetails = response.getOrThrow()
                val formattedDetails = formatMovieDetailsUseCase(movieDetails)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null,
                    error = false,
                    movieDetails = formattedDetails
                )
            } catch (e: Exception) {
                val errorMsg = e.message ?: Constants.UNKNOWN_ERROR
                Log.e("MovieDetailsViewModel", errorMsg)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = true,
                    errorMessage = Constants.UNKNOWN_ERROR
                )
            }
        }
    }
}