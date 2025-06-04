package com.example.movieapp.networking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.interfaces.GenreTypeSelected
import com.example.movieapp.networking.instance.MovieInstance
import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.Movies
import com.example.movieapp.status
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel : ViewModel(), GenreTypeSelected {

    private val _moviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val moviesState: StateFlow<MoviesState> = _moviesState.asStateFlow()

    private val _genreTypeSelected = MutableStateFlow<GenresState>(GenresState.NotSelected)
    val genreTypeSelected: StateFlow<GenresState> = _genreTypeSelected.asStateFlow()

    private val apiService = MovieInstance.api

    sealed class MoviesState {
        data object Loading : MoviesState()
        data class Success(val movies: Movies, val genres: GenresList) : MoviesState()
        data class Error(val message: String) : MoviesState()
    }

    sealed class GenresState {
        data object NotSelected : GenresState()
        data class Selected(val genresType: Int) : GenresState()
    }

    fun fetchMovies() {
        _moviesState.value = MoviesState.Loading
        viewModelScope.launch {
            try {
                if (status == ConnectivityObserver.Status.Unavailable) {
                    _moviesState.value = MoviesState.Error("No Internet Connection")
                    return@launch
                }
                val responseMovies = apiService.getMovies()
                if (responseMovies.isSuccessful && responseMovies.body() != null) {
                    val responseGenres = apiService.getGenres()
                    if (responseGenres.isSuccessful && responseGenres.body() != null) {
                        _moviesState.value = MoviesState.Success(responseMovies.body()!!, responseGenres.body()!!)
                    } else {
                        _moviesState.value = MoviesState.Error("Error: ${responseGenres.code()} ${responseGenres.message()}")
                    }
                } else {
                    _moviesState.value = MoviesState.Error("Error: ${responseMovies.code()} ${responseMovies.message()}")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error("Exception: ${e.message ?: "Unknown error"}")
            }
        }
    }

    override fun onGenreTypeSelected(genreId: Int) {
        _genreTypeSelected.value = GenresState.Selected(genreId)
    }
}