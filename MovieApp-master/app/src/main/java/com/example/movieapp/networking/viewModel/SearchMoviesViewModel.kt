package com.example.movieapp.networking.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.koin.MoviesRepository
import com.example.movieapp.networking.model.movies.MovieData
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchMoviesViewModel(
    private val repository: MoviesRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _moviesState = MutableStateFlow<SearchedMoviesState>(SearchedMoviesState.Loading)
    private var moviesState: StateFlow<SearchedMoviesState> = _moviesState.asStateFlow()

    var moviesList = ArrayList<MovieData>()

    var isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    sealed class SearchedMoviesState {
        data object Loading : SearchedMoviesState()
        data class Success(val movies: List<MovieData>) : SearchedMoviesState()
        data class Error(val message: String) : SearchedMoviesState()
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _moviesState.value = SearchedMoviesState.Error("No Internet Connection")
                    return@launch
                }
                _moviesState.value = SearchedMoviesState.Loading
                val responseMovies = repository.searchMovie(query)
                if (responseMovies.isSuccessful && responseMovies.body() != null) {
                    val moviesData = responseMovies.body()!!
                    _moviesState.value = SearchedMoviesState.Success(moviesData.results)
                } else {
                    _moviesState.value = SearchedMoviesState.Error("Error: ${responseMovies.code()} ${responseMovies.message()}")
                }
            } catch (e: Exception) {
                _moviesState.value = SearchedMoviesState.Error("Exception: ${e.message ?: "Unknown error"}")
            } finally {
                observeMoviesSearched()
            }
        }
    }

    private fun observeMoviesSearched() {
        viewModelScope.launch {
            moviesState.collect { it ->
                when (it) {
                    is SearchedMoviesState.Error -> {
                        errorMessage.value = it.message
                        isError.value = true
                        isLoading.value = false
                        isSuccess.value = false
                    }

                    is SearchedMoviesState.Loading -> {
                        isLoading.value = true
                        isError.value = false
                    }

                    is SearchedMoviesState.Success -> {
                        moviesList = it.movies as ArrayList<MovieData>
                        isLoading.value = false
                        isError.value = false
                        isSuccess.value = true
                    }
                }
            }
        }
    }
}