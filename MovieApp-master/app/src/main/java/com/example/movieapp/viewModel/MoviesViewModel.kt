package com.example.movieapp.viewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.interfaces.GenreTypeSelected
import com.example.movieapp.koin.MoviesRepository
import com.example.movieapp.networking.model.genres.Genre
import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.MovieData
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val repository: MoviesRepository,
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

    fun fetchMovies() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = false)
            try {
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = true,
                            errorMessage = "No Internet Connection"
                        )
                    return@launch
                }
                val pageToFetch = ++currentPage
                val responseMovies = repository.fetchMovies(pageToFetch)
                if (responseMovies.isSuccessful && responseMovies.body() != null) {
                    val moviesData = responseMovies.body()!!
                    val responseGenres = repository.fetchGenres()
                    if (responseGenres.isSuccessful && responseGenres.body() != null) {
                        _uiState.value =
                            MoviesUiState(
                                isLoading = false,
                                isSuccess = true,
                                movies = moviesData.results as ArrayList<MovieData>,
                                genres = responseGenres.body()!!
                            )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = true,
                            errorMessage = "Error: ${responseGenres.code()} ${responseGenres.message()}"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = true,
                        errorMessage = "Error: ${responseMovies.code()} ${responseMovies.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = true,
                    errorMessage = "Exception: ${e.message ?: "Unknown error"}"
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
                            movie.genre_ids.contains(genreType.intValue)
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