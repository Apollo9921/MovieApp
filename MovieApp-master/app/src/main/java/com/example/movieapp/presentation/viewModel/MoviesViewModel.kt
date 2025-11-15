package com.example.movieapp.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.presentation.interaction.GenreTypeSelected
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.core.utils.Constants
import com.example.movieapp.core.utils.network.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel(), GenreTypeSelected {

    private val _genreTypeSelected = MutableStateFlow<GenresState>(GenresState.NotSelected)
    private val genreTypeSelected: StateFlow<GenresState> = _genreTypeSelected.asStateFlow()

    private val _uiState = MutableStateFlow(MoviesUiState(isLoading = false))
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
        var movies: ArrayList<MovieData> = ArrayList(),
        val genres: GenresList = GenresList(emptyList()),
    )

    sealed class GenresState {
        data object NotSelected : GenresState()
        data class Selected(val genresType: Int) : GenresState()
    }

    init {
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status == ConnectivityObserver.Status.Available && moviesList.isEmpty()) {
                    fetchMovies()
                } else if (status == ConnectivityObserver.Status.Unavailable) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION
                    )
                }
            }
        }
    }

    fun fetchMovies() {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = false)
        viewModelScope.launch {
            try {
                val pageToFetch = currentPage + 1
                val moviesResult = getMoviesUseCase(pageToFetch).first()
                val genresResult = getGenresUseCase().first()

                if (moviesResult.isSuccess && genresResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null,
                        error = false,
                        genres = genresResult.getOrThrow(),
                        movies = moviesResult.getOrThrow().results as ArrayList<MovieData>,
                    )
                    currentPage = pageToFetch
                } else {
                    val errorMsg = moviesResult.exceptionOrNull()?.message
                        ?: genresResult.exceptionOrNull()?.message
                        ?: Constants.UNKNOWN_ERROR

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = true,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                Log.e("MoviesViewModel", e.message.toString())
                val errorMsg = e.message ?: Constants.UNKNOWN_ERROR
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = true,
                    errorMessage = errorMsg
                )
            } finally {
                observeMovies()
                observeGenres()
            }
        }
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