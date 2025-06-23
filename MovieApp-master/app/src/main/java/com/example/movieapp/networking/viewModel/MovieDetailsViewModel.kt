package com.example.movieapp.networking.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.koin.MoviesRepository
import com.example.movieapp.networking.model.details.MovieDetails
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieRepository: MoviesRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _movieDetailState =
        MutableStateFlow<MovieDetailState>(MovieDetailState.Error("Unknown Error"))
    private val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState.asStateFlow()

    var isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    var movieDetails: MovieDetails? = null

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )


    sealed class MovieDetailState {
        data class Success(val movieDetails: MovieDetails) : MovieDetailState()
        data class Error(val message: String) : MovieDetailState()
    }

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _movieDetailState.value = MovieDetailState.Error("No Internet Connection")
                    isLoading.value = false
                    return@launch
                }
                val response = movieRepository.getMovieDetails(movieId)
                if (response.isSuccessful && response.body() != null) {
                    _movieDetailState.value = MovieDetailState.Success(response.body()!!)
                } else {
                    _movieDetailState.value =
                        MovieDetailState.Error("Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _movieDetailState.value =
                    MovieDetailState.Error("Exception: ${e.message ?: "Unknown error"}")
            } finally {
                observeMovieDetails()
            }
        }
    }

    private fun observeMovieDetails() {
        viewModelScope.launch {
            movieDetailState.collect {
                when (it) {
                    is MovieDetailState.Error -> {
                        errorMessage.value = it.message
                        isError.value = true
                        isLoading.value = false
                    }
                    is MovieDetailState.Success -> {
                        movieDetails = it.movieDetails
                        isLoading.value = false
                        isError.value = false
                        isSuccess.value = true
                    }
                }
            }
        }
    }
}