package com.example.movieapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.movieapp.R
import com.example.movieapp.core.Background
import com.example.movieapp.networking.model.genres.Genre
import com.example.movieapp.networking.model.movies.MovieData
import com.example.movieapp.core.White
import com.example.movieapp.networking.instance.MovieInstance
import com.example.movieapp.viewModel.MoviesViewModel
import com.example.movieapp.viewModel.ScreenSizingViewModel
import com.example.movieapp.viewModel.SearchMoviesViewModel

@Composable
fun MoviesList(
    pv: PaddingValues,
    movies: ArrayList<MovieData>,
    genresList: ArrayList<Genre>?,
    filteredMovies: List<MovieData>,
    genreSelected: Int,
    viewModel: ViewModel,
    navController: NavController,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    when (viewModel) {
        is MoviesViewModel -> {
            viewModel
        }

        else -> viewModel as? SearchMoviesViewModel
    }

    val imageLoadingStates = remember { mutableStateMapOf<String, AsyncImagePainter.State>() }
    var allImagesLoaded by remember { mutableStateOf(false) }
    val currentMovies = if (filteredMovies.isNotEmpty() || genreSelected != 0) filteredMovies else movies

    val lazyGridState = rememberLazyGridState()
    val moviePosition by remember {
        derivedStateOf {
            val layoutInfo = lazyGridState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                0
            } else {
                visibleItemsInfo.last().index + 1
            }
        }
    }

    LaunchedEffect(imageLoadingStates.toMap(), currentMovies) {
        allImagesLoaded = if (currentMovies.isNotEmpty()) {
            currentMovies.all { movie ->
                val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movie.poster_path}"
                imageLoadingStates[imageUrl] is AsyncImagePainter.State.Success || imageLoadingStates[imageUrl] is AsyncImagePainter.State.Error
            }
        } else {
            true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(pv)
    ) {
        if (genresList?.isNotEmpty() == true) {
            GenresListScreen(genresList, genreSelected, viewModel as MoviesViewModel, screenMetrics, screenViewModel)
        }
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(3)
        ) {
            items(currentMovies.size) { index ->
                val movie = currentMovies[index]
                val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movie.poster_path}"

                AsyncImage(
                    model = imageUrl,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    onError = { state ->
                        imageLoadingStates[imageUrl] = state
                    },
                    onSuccess = { state ->
                        imageLoadingStates[imageUrl] = state
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(2f / 3f)
                        .fillMaxSize()
                        .padding(10.dp)
                        .clickable {
                            navController.navigate("details_screen/${movie.id}")
                        }
                )

                if (filteredMovies.isEmpty() && genresList?.isNotEmpty() == true) {
                    if (index == movies.size - 1 && allImagesLoaded) {
                        viewModel as MoviesViewModel
                        viewModel.fetchMovies()
                    }
                }
            }
        }
    }
    DisplayMoviePosition(currentMovies.size, moviePosition, screenMetrics, screenViewModel)
}

@Composable
private fun DisplayMoviePosition(
    moviesSize: Int,
    moviePosition: Int = 0,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val bottomSize = screenViewModel.calculateCustomHeight(baseSize = 50, screenMetrics).dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomSize),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { },
            containerColor = White,
            contentColor = Background,
            shape = CircleShape,
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp),
            content = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "$moviePosition")
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Background,
                        modifier = Modifier.width(20.dp)
                    )
                    Text(text = "$moviesSize")
                }
            }
        )
    }
}