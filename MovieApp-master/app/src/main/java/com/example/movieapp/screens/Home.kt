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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.utils.size.ScreenSizeUtils
import org.koin.androidx.compose.koinViewModel

private var moviesViewModel: MoviesViewModel? = null
private var isConnected = mutableStateOf(false)

@Composable
fun HomeScreen(navController: NavController) {
    moviesViewModel = koinViewModel<MoviesViewModel>()
    if (status.value == ConnectivityObserver.Status.Available && !isConnected.value) {
        isConnected.value = true
        fetchMovies()
    }

    val isLoading = moviesViewModel?.isLoading?.value
    val isSuccess = moviesViewModel?.isSuccess?.value
    val isError = moviesViewModel?.isError?.value
    val errorMessage = moviesViewModel?.errorMessage?.value

    val moviesList = moviesViewModel?.moviesList ?: ArrayList()
    val genresList = moviesViewModel?.genresList
    val filteredMovies = moviesViewModel?.filteredMovies ?: emptyList()
    val genreType = moviesViewModel?.genreType?.intValue ?: 0

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { HomeTopBar() },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            if (isLoading == true || isSuccess == true) {
                MoviesList(
                    it,
                    moviesList,
                    genresList,
                    filteredMovies,
                    genreType,
                    moviesViewModel!!
                )
            } else if (isError == true) {
                ErrorScreen(errorMessage)
            }
        }
    )
}

private fun fetchMovies() {
    CoroutineScope(Dispatchers.IO).launch {
        moviesViewModel?.fetchMovies()
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