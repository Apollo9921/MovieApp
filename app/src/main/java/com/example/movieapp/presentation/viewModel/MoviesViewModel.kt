package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.presentation.interaction.GenreTypeSelected
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.movies.Movies
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel(), GenreTypeSelected {

    private val _genreTypeSelected = MutableStateFlow<GenresState>(GenresState.NotSelected)
    private val genreTypeSelected: StateFlow<GenresState> = _genreTypeSelected.asStateFlow()

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    private var currentPage = 0

    data class MoviesUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: Boolean = false,
        var errorMessage: String? = null,
        var movies: List<MovieData> = emptyList(),
        var filteredMovies: List<MovieData> = emptyList(),
        val genres: GenresList = GenresList(emptyList()),
        var genreType: Int = 0
    )

    sealed class GenresState {
        data object NotSelected : GenresState()
        data class Selected(val genresType: Int) : GenresState()
    }

    init {
        definingUiState(
            isLoading = true,
            isSuccess = false,
            error = false,
            errorMessage = null
        )
        fetchMovies()
        observeGenres()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                val pageToFetch = currentPage + 1
                val moviesResult = getMoviesUseCase(pageToFetch, _uiState.value.movies).first()
                val genresResult = getGenresUseCase().first()
                if (moviesResult.isSuccess && genresResult.isSuccess) {
                    fetchMoviesSuccess(moviesResult, genresResult)
                } else {
                    fetchMoviesFailure(moviesResult, genresResult)
                }
            } catch (e: Exception) {
                fetchMoviesException(e)
            }
        }
    }

    private fun fetchMoviesSuccess(
        moviesResult: Result<Movies>,
        genresResult: Result<GenresList>
    ) {
        Log.e("MoviesViewModel", "Movies fetched successfully")
        if (moviesResult.getOrThrow().results.isEmpty()) {
            definingUiState(
                isLoading = false,
                isSuccess = false,
                error = true,
                errorMessage = Constants.NO_MOVIES_FOUND
            )
        } else {
            val updatedMovies = moviesResult.getOrThrow().results
            val genres = genresResult.getOrThrow().genres.toMutableList()
            if (genres.firstOrNull()?.id != 0) {
                genres.add(0, Genre(0, "All"))
            }
            val updatedGenres = genresResult.getOrThrow().copy(genres = genres)

            _uiState.update { currentState ->
                val totalList = (currentState.movies + updatedMovies).distinctBy { it.id }
                currentState.copy(
                    isLoading = false,
                    isSuccess = true,
                    movies = totalList,
                    genres = updatedGenres
                )
            }
            currentPage = moviesResult.getOrThrow().page
        }
    }

    private fun fetchMoviesFailure(moviesResult: Result<Movies>, genresResult: Result<GenresList>) {
        checkIfMoviesListIsNotEmpty()
        val errorMsg =
            if (moviesResult.exceptionOrNull() is ConnectException || genresResult.exceptionOrNull() is ConnectException) {
                Constants.NO_INTERNET_CONNECTION
            } else {
                Constants.UNKNOWN_ERROR
            }
        Log.e("MoviesViewModel", errorMsg)
        definingUiState(
            isLoading = false,
            isSuccess = null,
            error = true,
            errorMessage = errorMsg
        )
    }

    private fun fetchMoviesException(e: Exception) {
        checkIfMoviesListIsNotEmpty()
        val errorMsg =
            if (e is ConnectException) Constants.NO_INTERNET_CONNECTION else Constants.UNKNOWN_ERROR
        Log.e("MoviesViewModel", errorMsg)
        definingUiState(
            isLoading = false,
            isSuccess = null,
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
            error = error ?: _uiState.value.error,
            errorMessage = errorMessage
        )
    }

    private fun checkIfMoviesListIsNotEmpty() {
        if (_uiState.value.movies.isNotEmpty()) {
            definingUiState(
                isLoading = false,
                isSuccess = true,
                error = false,
                errorMessage = null
            )
            return
        }
    }

    private fun observeGenres() {
        viewModelScope.launch {
            genreTypeSelected.collect { state ->
                val currentMovies = _uiState.value.movies
                when (state) {
                    GenresState.NotSelected -> {
                        _uiState.update { it.copy(filteredMovies = emptyList(), genreType = 0) }
                    }
                    is GenresState.Selected -> {
                        _uiState.update { it.copy(
                            genreType = state.genresType,
                            filteredMovies = currentMovies.filter { movie ->
                                movie.genreIds.contains(state.genresType)
                            }
                        )}
                    }
                }
            }
        }
    }

    override fun onGenreTypeSelected(genreId: Int) {
        if (genreId == 0) {
            _genreTypeSelected.value = GenresState.NotSelected
        } else {
            _genreTypeSelected.value = GenresState.Selected(genreId)
        }
    }
}