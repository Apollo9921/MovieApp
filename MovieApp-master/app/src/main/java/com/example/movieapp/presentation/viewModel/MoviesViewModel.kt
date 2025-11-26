package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
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

    var moviesList = ArrayList<MovieData>()
    var genresList = ArrayList<Genre>()
    var filteredMovies = emptyList<MovieData>()
    var genreType = mutableIntStateOf(0)

    data class MoviesUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: Boolean = false,
        var errorMessage: String? = null,
        var movies: List<MovieData> = emptyList(),
        val genres: GenresList = GenresList(emptyList()),
    )

    sealed class GenresState {
        data object NotSelected : GenresState()
        data class Selected(val genresType: Int) : GenresState()
    }

    init {
        fetchMoviesFistTime()
    }

    private fun fetchMoviesFistTime() {
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status == ConnectivityObserver.Status.Available && moviesList.isEmpty()) {
                    definingUiState(
                        isLoading = true,
                        isSuccess = null,
                        error = false,
                        errorMessage = null
                    )
                    fetchMovies()
                } else if (status == ConnectivityObserver.Status.Unavailable) {
                    definingUiState(
                        isLoading = false,
                        isSuccess = false,
                        error = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION
                    )
                }
            }
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                val pageToFetch = currentPage + 1
                val moviesResult = getMoviesUseCase(pageToFetch).first()
                val genresResult = getGenresUseCase().first()
                if (moviesResult.isSuccess && genresResult.isSuccess) {
                   fetchMoviesSuccess(moviesResult, genresResult, pageToFetch)
                } else {
                    fetchMoviesFailure(moviesResult, genresResult)
                }
            } catch (e: Exception) {
                fetchMoviesException(e)
            } finally {
                observeMovies()
                observeGenres()
            }
        }
    }

    private fun fetchMoviesSuccess(
        moviesResult: Result<Movies>,
        genresResult: Result<GenresList>,
        pageToFetch: Int
    ) {
        Log.e("MoviesViewModel", "Movies fetched successfully")
        if (moviesResult.getOrThrow().results.isEmpty()) {
            definingUiState(
                isLoading = false,
                isSuccess = null,
                error = true,
                errorMessage = Constants.NO_MOVIES_FOUND
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null,
                error = false,
                genres = genresResult.getOrThrow(),
                movies = moviesResult.getOrThrow().results,
            )
        }
        currentPage = pageToFetch
    }

    private fun fetchMoviesFailure(moviesResult: Result<Movies>, genresResult: Result<GenresList>) {
        if (_uiState.value.movies.isNotEmpty()) {
            definingUiState(
                isLoading = false,
                isSuccess = true,
                error = false,
                errorMessage = null
            )
            return
        }
        val errorMsg =
            if (moviesResult.exceptionOrNull() is ConnectException || genresResult.exceptionOrNull() is ConnectException) {
                Constants.NO_INTERNET_CONNECTION
            } else {
                moviesResult.exceptionOrNull()?.message
                    ?: genresResult.exceptionOrNull()?.message
            } ?: Constants.UNKNOWN_ERROR
        Log.e("MoviesViewModel", errorMsg)
        definingUiState(
            isLoading = false,
            isSuccess = null,
            error = true,
            errorMessage = errorMsg
        )
    }

    private fun fetchMoviesException(e: Exception) {
        if (_uiState.value.movies.isNotEmpty()) {
            definingUiState(
                isLoading = false,
                isSuccess = true,
                error = false,
                errorMessage = null
            )
            return
        }
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

    private fun observeMovies() {
        val newMovies = ArrayList<MovieData>()
        newMovies.addAll(moviesList)
        newMovies.addAll(uiState.value.movies)
        moviesList = newMovies.distinctBy { it.id } as ArrayList<MovieData>

        val newGenresList = ArrayList<Genre>()
        val sourceGenres = uiState.value.genres.genres
        newGenresList.add(Genre(0, "All"))
        newGenresList.addAll(sourceGenres)
        genresList = newGenresList.distinctBy { it.name } as ArrayList<Genre>
    }

    private fun observeGenres() {
        viewModelScope.launch {
            genreTypeSelected.collect {
                when (it) {
                    GenresState.NotSelected -> {
                        genreType.intValue = 0
                        filteredMovies = emptyList()
                    }

                    is GenresState.Selected -> {
                        genreType.intValue = it.genresType
                        filteredMovies = moviesList.filter { movie ->
                            movie.genreIds.contains(genreType.intValue)
                        }
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