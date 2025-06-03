package com.example.movieapp.screens

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.networking.model.Movies
import com.example.movieapp.networking.viewModel.MoviesViewModel
import com.example.movieapp.utils.MoviesList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private var moviesViewModel: MoviesViewModel = MoviesViewModel()
private var moviesList: Movies? = null
private var isLoading = mutableStateOf(false)
private var errorMessage = mutableStateOf("")
private var isSuccess = mutableStateOf(false)

@Composable
fun HomeScreen(backStack: NavBackStack?) {
    fetchMovies()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { HomeTopBar() },
        content = {
            if (isLoading.value || isSuccess.value) {
                MoviesList(it, moviesList)
            } else {
                Log.e("HomeScreen", "Error: ${errorMessage.value}")
            }
        }
    )
}

private fun fetchMovies() {
    CoroutineScope(Dispatchers.IO).launch {
        moviesViewModel.fetchMovies()
        observeMovies()
    }
}

private suspend fun observeMovies() {
    moviesViewModel.moviesState.collect {
        when (it) {
            is MoviesViewModel.MoviesState.Error -> {
                errorMessage.value = it.message
                isLoading.value = false
            }
            is MoviesViewModel.MoviesState.Loading -> {
                isLoading.value = true
            }
            is MoviesViewModel.MoviesState.Success -> {
                moviesList = it.movies
                isLoading.value = false
                isSuccess.value = true
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
        Text(
            style = Typography.titleLarge,
            text = "Home"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() {
    HomeScreen(null)
}