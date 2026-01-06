package com.example.movieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetFavoriteMoviesUseCase
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.UpdateFavoritesMoviesPositionUseCase
import com.example.movieapp.presentation.interaction.GenreTypeSelected
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Collections

class FavoritesViewModel(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val updateFavoritesMoviesPositionUseCase: UpdateFavoritesMoviesPositionUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel(), GenreTypeSelected {

    private val _uiState = MutableStateFlow(FavoritesMoviesUiState())
    val uiState: StateFlow<FavoritesMoviesUiState> = _uiState.asStateFlow()

    private val _genreTypeSelected = MutableStateFlow(GenresState())
    val genreTypeSelected: StateFlow<GenresState> = _genreTypeSelected.asStateFlow()

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    data class FavoritesMoviesUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val isError: Boolean = false,
        var errorMessage: String? = null,
        var moviesList: List<MovieData> = emptyList(),
        var genresList: List<Genre> = emptyList(),
        var filteredMovies: List<MovieData> = emptyList(),
    )

    data class GenresState(
        val genresType: Int = 0
    )

    init {
        getFavoriteMovies()
    }

    fun getFavoriteMovies() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isSuccess = false,
            isError = false,
            errorMessage = null
        )
        viewModelScope.launch {
            networkStatus.collect { status ->
                val genresResult = getGenresUseCase().first()
                if (status == ConnectivityObserver.Status.Unavailable && genresResult.getOrNull()?.genres.isNullOrEmpty() == true) {
                    _uiState.value = _uiState.value.copy(
                        isSuccess = false,
                        isLoading = false,
                        isError = true,
                        errorMessage = Constants.NO_INTERNET_CONNECTION
                    )
                    return@collect
                }
                getFavoriteMoviesUseCase().collect { result ->
                    if (result.isSuccess) {
                        getFavoritesMoviesSuccess(result, genresResult)
                    } else if (result.isFailure || result.getOrNull().isNullOrEmpty()) {
                        getFavoritesMoviesFailure(result)
                    }
                }
            }
        }
    }

    private fun getFavoritesMoviesSuccess(
        result: Result<List<MovieData>>,
        genresResult: Result<GenresList>
    ) {
        val moviesList = result.getOrNull() ?: emptyList()
        var genresList = genresResult.getOrNull()?.genres ?: emptyList()

        if (genresList.isNotEmpty() && genresList[0].id != 0) {
            val mutableGenres = genresList.toMutableList()
            mutableGenres.add(0, Genre(0, "All"))
            genresList = mutableGenres
        }

        if (moviesList.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isSuccess = false,
                isLoading = false,
                isError = true,
                errorMessage = Constants.NO_MOVIES_FOUND
            )
            return
        }
        _uiState.value = _uiState.value.copy(
            moviesList = moviesList,
            genresList = genresList,
            isSuccess = true,
            isLoading = false,
            isError = false,
            errorMessage = null
        )
    }

    private fun getFavoritesMoviesFailure(result: Result<List<MovieData>>) {
        val errorMessage = result.exceptionOrNull()?.message
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
            isLoading = false,
            isError = true,
            errorMessage = errorMessage
        )
    }

    fun moveMovie(from: Int, to: Int) {
        val currentList = _uiState.value.moviesList
        val updatedList = currentList.toMutableList()
        val positionDraggedMovie = currentList[from].voteCount
        val positionMovie = currentList[to].voteCount
        updatedList[from] = updatedList[from].copy(voteCount = positionMovie)
        updatedList[to] = updatedList[to].copy(voteCount = positionDraggedMovie)
        Collections.swap(updatedList, from, to)
        _uiState.value = _uiState.value.copy(
            moviesList = updatedList
        )
    }

    fun updateMoviePosition() {
        viewModelScope.launch {
            updateFavoritesMoviesPositionUseCase(uiState.value.moviesList).first()
        }
    }

    override fun onGenreTypeSelected(genreId: Int) {
        viewModelScope.launch {
            if (genreId == 0) {
                _genreTypeSelected.value = _genreTypeSelected.value.copy(
                    genresType = 0
                )
                _uiState.value = _uiState.value.copy(
                    filteredMovies = emptyList()
                )
            } else {
                _uiState.value.filteredMovies = _uiState.value.moviesList.filter { movie ->
                    movie.genreIds.contains(genreId)
                }
                _genreTypeSelected.value = _genreTypeSelected.value.copy(
                    genresType = genreId
                )
                _uiState.value = _uiState.value.copy(
                    filteredMovies = _uiState.value.filteredMovies
                )
            }
        }
    }
}