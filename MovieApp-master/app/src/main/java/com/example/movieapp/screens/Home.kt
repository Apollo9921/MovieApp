package com.example.movieapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.networking.model.movies.Movies
import com.example.movieapp.networking.viewModel.MoviesViewModel
import com.example.movieapp.status
import com.example.movieapp.components.ErrorScreen
import com.example.movieapp.components.MoviesList
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.movieapp.networking.model.movies.MovieData
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.unit.sp
import com.example.movieapp.networking.model.genres.Genre
import com.example.movieapp.utils.size.ScreenSizeUtils
import org.koin.androidx.compose.koinViewModel

private var moviesViewModel: MoviesViewModel? = null
private var moviesList: Movies? = null
private var filteredMovies: List<MovieData> = emptyList()
private var genresList: ArrayList<Genre>? = ArrayList()
private var genreType = mutableIntStateOf(0)
private var isLoading = mutableStateOf(false)
private var isSuccess = mutableStateOf(false)
private var isError = mutableStateOf(false)
private var errorMessage = mutableStateOf("")

@Composable
fun HomeScreen(backStack: NavBackStack?) {
    moviesViewModel = koinViewModel<MoviesViewModel>()
    fetchMovies()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { HomeTopBar() },
        content = {
            if (isLoading.value || isSuccess.value) {
                MoviesList(
                    it,
                    moviesList,
                    genresList,
                    filteredMovies,
                    genreType.intValue,
                    moviesViewModel!!
                )
            } else {
                ErrorScreen()
            }
        }
    )

    LaunchedEffect(status.value) {
        if (status.value == ConnectivityObserver.Status.Available) {
            fetchMovies()
        }
    }
    LaunchedEffect(moviesViewModel?.genreTypeSelected?.collectAsState()?.value) {
        observeGenres()
    }
}

private fun fetchMovies() {
    CoroutineScope(Dispatchers.IO).launch {
        moviesViewModel?.fetchMovies()
        observeMovies()
    }
}

private suspend fun observeMovies() {
    moviesViewModel?.moviesState?.collect { it ->
        when (it) {
            is MoviesViewModel.MoviesState.Error -> {
                errorMessage.value = it.message
                isError.value = true
                isLoading.value = false
            }

            is MoviesViewModel.MoviesState.Loading -> {
                isLoading.value = true
                isError.value = false
            }

            is MoviesViewModel.MoviesState.Success -> {
                moviesList = it.movies
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

private suspend fun observeGenres() {
    moviesViewModel?.genreTypeSelected?.collect { it ->
        when (it) {
            is MoviesViewModel.GenresState.NotSelected -> {
                genreType.intValue = 0
                filteredMovies = emptyList()
            }

            is MoviesViewModel.GenresState.Selected -> {
                genreType.intValue = it.genresType
                if (moviesList != null) {
                    filteredMovies = moviesList?.results?.filter {
                        it.genre_ids.contains(genreType.intValue)
                    }!!
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarBackground)
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .safeDrawingPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = "Home"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() {
    HomeScreen(null)
}