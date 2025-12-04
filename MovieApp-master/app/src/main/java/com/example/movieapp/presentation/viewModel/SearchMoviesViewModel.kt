package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetSearchUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException

class SearchMoviesViewModel(
    private val getSearchUseCase: GetSearchUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchMovieUiState())
    val uiState: StateFlow<SearchMovieUiState> = _uiState.asStateFlow()

    data class SearchMovieUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val isError: Boolean = false,
        var errorMessage: String? = null,
        var moviesList: List<MovieData> = emptyList(),
        var query: String = ""
    )

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    init {
        observeQueryChanges()
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        if (_uiState.value.query.isEmpty() && networkStatus.value == ConnectivityObserver.Status.Unavailable) {
            searchMoviesException(ConnectException())
            return
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQueryChanges() {
        viewModelScope.launch {
            _uiState
                .debounce(500L)
                .distinctUntilChanged { old, new ->
                    old.query == new.query
                }
                .collectLatest { state ->
                    if (state.query.isEmpty() && networkStatus.value == ConnectivityObserver.Status.Available) {
                        definingUiState(
                            isLoading = false,
                            isSuccess = false,
                            error = true,
                            errorMessage = Constants.NO_MOVIES_FOUND
                        )
                        _uiState.value = _uiState.value.copy(
                            moviesList = ArrayList()
                        )
                    } else {
                        searchMovies(state.query)
                    }
                }
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                definingUiState(
                    isLoading = true,
                    isSuccess = false,
                    error = false,
                    errorMessage = null
                )
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    searchMoviesException(ConnectException())
                    return@launch
                }
                val responseMovies = getSearchUseCase(query).first()
                if (responseMovies.isSuccess) {
                    val moviesData = responseMovies.getOrNull()?.results ?: emptyList()
                    searchMoviesSuccess(moviesData)
                } else {
                    searchMoviesFailure(responseMovies)
                }
            } catch (e: Exception) {
                searchMoviesException(e)
            }
        }
    }

    private fun searchMoviesSuccess(moviesData: List<MovieData>) {
        if (moviesData.isEmpty()) {
            definingUiState(
                isLoading = false,
                isSuccess = false,
                error = true,
                errorMessage = Constants.NO_MOVIES_FOUND
            )
            return
        }
        definingUiState(
            isLoading = false,
            isSuccess = true,
            error = false,
            errorMessage = null
        )
        _uiState.value = _uiState.value.copy(
            moviesList = moviesData
        )
    }

    private fun searchMoviesFailure(responseMovies: Result<Movies>) {
        val errorMsg =
            if (responseMovies.exceptionOrNull() is ConnectException) Constants.NO_INTERNET_CONNECTION
            else responseMovies.exceptionOrNull()?.message ?: Constants.UNKNOWN_ERROR
        Log.e("SearchMoviesViewModel", errorMsg)
        definingUiState(
            isLoading = false,
            isSuccess = false,
            error = true,
            errorMessage = errorMsg
        )
    }

    private fun searchMoviesException(e: Exception) {
        val errorMsg =
            if (e is ConnectException) Constants.NO_INTERNET_CONNECTION else Constants.UNKNOWN_ERROR
        Log.e("SearchMoviesViewModel", e.message ?: errorMsg)
        definingUiState(
            isLoading = false,
            isSuccess = false,
            error = true,
            errorMessage = errorMsg
        )
    }

    private fun definingUiState(
        isLoading: Boolean?,
        isSuccess: Boolean?,
        error: Boolean?,
        errorMessage: String?
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading ?: _uiState.value.isLoading,
            isSuccess = isSuccess ?: _uiState.value.isSuccess,
            isError = error ?: _uiState.value.isError,
            errorMessage = errorMessage
        )
    }
}