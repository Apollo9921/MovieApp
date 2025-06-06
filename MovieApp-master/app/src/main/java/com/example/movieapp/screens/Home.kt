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
import kotlin.collections.distinctBy

private var moviesViewModel: MoviesViewModel? = null
private var moviesList: ArrayList<MovieData> = ArrayList()
private var filteredMovies: List<MovieData> = emptyList()
private var genresList: ArrayList<Genre>? = ArrayList()
private var genreType = mutableIntStateOf(0)
private var isLoading = mutableStateOf(false)
private var isSuccess = mutableStateOf(false)
private var isError = mutableStateOf(false)
private var errorMessage = mutableStateOf("")
private var isConnected = mutableStateOf(false)

@Composable
fun HomeScreen(backStack: NavBackStack?) {
    moviesViewModel = koinViewModel<MoviesViewModel>()
    if (status.value == ConnectivityObserver.Status.Available && !isConnected.value) {
        isConnected.value = true
        fetchMovies()
    }
    LaunchedEffect(moviesViewModel?.genreTypeSelected?.collectAsState()?.value) {
        observeGenres()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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

private suspend fun observeGenres() {
    moviesViewModel?.genreTypeSelected?.collect {
        when (it) {
            is MoviesViewModel.GenresState.NotSelected -> {
                genreType.intValue = 0
                filteredMovies = emptyList()
            }

            is MoviesViewModel.GenresState.Selected -> {
                genreType.intValue = it.genresType
                filteredMovies = moviesList.filter { movie ->
                    movie.genre_ids.contains(genreType.intValue)
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