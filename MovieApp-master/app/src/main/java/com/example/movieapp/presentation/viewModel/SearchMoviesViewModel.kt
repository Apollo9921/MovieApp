package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.movies.MovieData
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
        var moviesList: ArrayList<MovieData> = ArrayList(),
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
                    if (state.query.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            moviesList = ArrayList(),
                            isLoading = false,
                            isSuccess = false,
                            isError = true,
                            errorMessage = Constants.NO_MOVIES_FOUND
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
                _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _uiState.value = _uiState.value.copy(
                        isError = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION
                    )
                    return@launch
                }
                val responseMovies = getSearchUseCase(query).first()
                val moviesData = responseMovies.getOrNull()?.results ?: emptyList()
                if (moviesData.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        isError = true,
                        errorMessage = Constants.NO_MOVIES_FOUND
                    )
                    return@launch
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null,
                    isError = false,
                    moviesList = moviesData as ArrayList<MovieData>
                )
            } catch (e: Exception) {
                val errorMsg = e.message ?: Constants.UNKNOWN_ERROR
                Log.e("SearchMoviesViewModel", errorMsg)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = Constants.UNKNOWN_ERROR
                )
            }
        }
    }
}