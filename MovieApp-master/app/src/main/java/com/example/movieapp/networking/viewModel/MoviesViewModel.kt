package com.example.movieapp.networking.viewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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

    private val _moviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    private val moviesState: StateFlow<MoviesState> = _moviesState.asStateFlow()

    private val _genreTypeSelected = MutableStateFlow<GenresState>(GenresState.NotSelected)
    private val genreTypeSelected: StateFlow<GenresState> = _genreTypeSelected.asStateFlow()

    var moviesList = ArrayList<MovieData>()
    var genresList = ArrayList<Genre>()
    var filteredMovies = emptyList<MovieData>()
    var genreType = mutableIntStateOf(0)

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

    private var currentPage = 0

    sealed class MoviesState {
        data object Loading : MoviesState()
        data class Success(val movies: List<MovieData>, val genres: GenresList) : MoviesState()
        data class Error(val message: String) : MoviesState()
    }

    sealed class GenresState {
        data object NotSelected : GenresState()
        data class Selected(val genresType: Int) : GenresState()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _moviesState.value = MoviesState.Error("No Internet Connection")
                    return@launch
                }
                _moviesState.value = MoviesState.Loading
                val pageToFetch = ++currentPage
                val responseMovies = repository.fetchMovies(pageToFetch)
                if (responseMovies.isSuccessful && responseMovies.body() != null) {
                    val moviesData = responseMovies.body()!!
                    val responseGenres = repository.fetchGenres()
                    if (responseGenres.isSuccessful && responseGenres.body() != null) {
                        _moviesState.value = MoviesState.Success(moviesData.results, responseGenres.body()!!)
                    } else {
                        _moviesState.value = MoviesState.Error("Error: ${responseGenres.code()} ${responseGenres.message()}")
                    }
                } else {
                    _moviesState.value = MoviesState.Error("Error: ${responseMovies.code()} ${responseMovies.message()}")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error("Exception: ${e.message ?: "Unknown error"}")
            } finally {
                observeMovies()
                observeGenres()
            }
        }
    }

    private fun observeMovies() {
        viewModelScope.launch {
            moviesState.collect { it ->
                when (it) {
                    is MoviesState.Error -> {
                        errorMessage.value = it.message
                        isError.value = true
                        isLoading.value = false
                        isSuccess.value = false
                    }
                    MoviesState.Loading -> {
                        isLoading.value = true
                        isError.value = false
                    }
                    is MoviesState.Success -> {
                        val newMovies = ArrayList<MovieData>()
                        newMovies.addAll(moviesList)
                        newMovies.addAll(it.movies)
                        moviesList = newMovies.distinctBy { it.id } as ArrayList<MovieData>

                        val newGenresList = ArrayList<Genre>()
                        val sourceGenres = it.genres.genres
                        newGenresList.add(Genre(0, "All"))
                        newGenresList.addAll(sourceGenres)
                        genresList = newGenresList.distinctBy { it.name } as ArrayList<Genre>
                        isLoading.value = false
                        isError.value = false
                        isSuccess.value = true
                    }
                }
            }
        }
    }

    private fun observeGenres() {
        viewModelScope.launch {
            genreTypeSelected.collect {
                when(it) {
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