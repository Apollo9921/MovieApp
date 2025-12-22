package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.details.FormattedMovieDetails
import com.example.movieapp.domain.model.details.MovieDetails
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.ToggleFavoriteUseCase
import com.example.movieapp.domain.usecase.FormatMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import com.example.movieapp.domain.usecase.IsMovieFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException

class MovieDetailsViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val formatMovieDetailsUseCase: FormatMovieDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    data class MovieDetailsUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: Boolean = false,
        var errorMessage: String? = null,
        val movieDetailsOriginal: MovieDetails? = null,
        var movieDetails: FormattedMovieDetails? = null,
        var movieId: Int = 0,
        val isFavorite: Boolean = false,
        val favoritesCount: Int = 0
    )

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    init {
        checkNetworkStatus()
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status == ConnectivityObserver.Status.Available && uiState.value.movieDetails == null) {
                    definingUiState(
                        isLoading = true,
                        isSuccess = false,
                        error = false,
                        errorMessage = null,
                        movieDetails = null,
                        movieDetailsOriginal = null
                    )
                    fetchMovieDetails(uiState.value.movieId)
                } else if (status == ConnectivityObserver.Status.Unavailable) {
                    definingUiState(
                        isLoading = false,
                        isSuccess = false,
                        error = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION,
                        movieDetails = null,
                        movieDetailsOriginal = null
                    )
                }
            }
        }
    }

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = getMovieDetailsUseCase(movieId).first()
                if (response.isSuccess && response.getOrNull() != null) {
                    fetchMoviesSuccess(response)
                } else {
                    fetchMovieDetailsFailure(response.exceptionOrNull() as Exception)
                }
            } catch (e: Exception) {
                fetchMovieDetailsFailure(e)
            }
        }
    }

    private fun fetchMoviesSuccess(response: Result<MovieDetails>) {
        Log.e("MovieDetailsViewModel", "Movie details fetched successfully")
        val movieDetails = response.getOrThrow()
        val formattedDetails = formatMovieDetailsUseCase(movieDetails)
        val checkIfDataNotEmpty =
            formatMovieDetailsUseCase.checkIfMovieDetailsNotEmpty(formattedDetails)
        if (!checkIfDataNotEmpty) {
            Log.e("MovieDetailsViewModel", Constants.NO_INFO_AVAILABLE)
            definingUiState(
                isLoading = false,
                isSuccess = false,
                error = true,
                errorMessage = Constants.NO_INFO_AVAILABLE,
                movieDetails = null,
                movieDetailsOriginal = movieDetails
            )
            return
        }
        checkIfMovieIsFavorite(uiState.value.movieId)
        definingUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null,
            movieDetailsOriginal = movieDetails,
            movieDetails = formattedDetails
        )
    }

    private fun fetchMovieDetailsFailure(e: Exception) {
        val errorMsg =
            if (e is ConnectException) Constants.NO_INTERNET_CONNECTION else Constants.UNKNOWN_ERROR
        Log.e("MovieDetailsViewModel", errorMsg)
        definingUiState(
            isLoading = false,
            isSuccess = false,
            error = true,
            errorMessage = errorMsg,
            movieDetails = null,
            movieDetailsOriginal = null
        )
    }

    fun toggleMovie(movie: MovieData) {
        viewModelScope.launch {
            val response = toggleFavoriteUseCase(movie).first()
            if (response.isSuccess) {
                Log.d("FavoritesViewModel", "Movie toggled to favorites: ${movie.title}")
                checkIfMovieIsFavorite(movie.id)
            } else {
                Log.e(
                    "FavoritesViewModel",
                    "Error toggled movie to favorites: ${response.exceptionOrNull()?.message}"
                )
            }
        }
    }

    private fun checkIfMovieIsFavorite(movieId: Int) {
        viewModelScope.launch {
            val response = isMovieFavoriteUseCase(movieId).first()
            if (response.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isFavorite = response.getOrNull() == true
                )
            } else {
                Log.e(
                    "FavoritesViewModel",
                    "Error checking if movie is favorite: ${response.exceptionOrNull()?.message}"
                )
                _uiState.value = _uiState.value.copy(
                    isFavorite = false
                )
            }
        }
    }

    private fun definingUiState(
        isLoading: Boolean?,
        isSuccess: Boolean?,
        error: Boolean?,
        errorMessage: String?,
        movieDetails: FormattedMovieDetails?,
        movieDetailsOriginal: MovieDetails?
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading ?: _uiState.value.isLoading,
            isSuccess = isSuccess ?: _uiState.value.isSuccess,
            error = error ?: _uiState.value.error,
            errorMessage = errorMessage,
            movieDetails = movieDetails ?: _uiState.value.movieDetails,
            movieDetailsOriginal = movieDetailsOriginal
        )
    }
}