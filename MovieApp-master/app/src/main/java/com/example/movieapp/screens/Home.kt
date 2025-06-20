package com.example.movieapp.screens

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.networking.viewModel.MoviesViewModel
import com.example.movieapp.components.ErrorScreen
import com.example.movieapp.components.MoviesList
import com.example.movieapp.utils.network.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.utils.size.ScreenSizeUtils
import org.koin.androidx.compose.koinViewModel

private var moviesViewModel: MoviesViewModel? = null
private var isConnected = mutableStateOf(false)

@Composable
fun HomeScreen(navController: NavController) {
    moviesViewModel = koinViewModel<MoviesViewModel>()
    val networkStatus = moviesViewModel?.networkStatus?.collectAsState()
    if (networkStatus?.value == ConnectivityObserver.Status.Available && !isConnected.value) {
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
            when {
                isLoading == true || isSuccess == true -> {
                    MoviesList(
                        it,
                        moviesList,
                        genresList,
                        filteredMovies,
                        genreType,
                        moviesViewModel!!
                    )
                }
                isError == true -> {
                    isConnected.value = false
                    if (errorMessage == stringResource(R.string.no_internet_connection) && moviesList.isNotEmpty()) {
                        MoviesList(
                            it,
                            moviesList,
                            genresList,
                            filteredMovies,
                            genreType,
                            moviesViewModel!!
                        )
                        Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG).show()
                        return@Scaffold
                    }
                    ErrorScreen(errorMessage)
                }
                networkStatus?.value == ConnectivityObserver.Status.Unavailable -> {
                    ErrorScreen(stringResource(R.string.no_internet_connection))
                }
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
            text = stringResource(R.string.home)
        )
    }
}