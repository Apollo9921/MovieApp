package com.example.movieapp.presentation.components

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
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.movieapp.R
import com.example.movieapp.data.network.instance.MovieInstance
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.theme.White
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun MoviesList(
    pv: PaddingValues = PaddingValues(0.dp),
    movies: List<MovieData>,
    genresList: List<Genre> = emptyList(),
    selectedGenreId: Int = 0,
    onMovieClick: (String) -> Unit,
    onGenreClick: (Int) -> Unit = {},
    onLoadMore: () -> Unit,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val imageLoadingStates = remember { mutableStateMapOf<String, AsyncImagePainter.State>() }
    var allImagesLoaded by remember { mutableStateOf(false) }

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

    LaunchedEffect(imageLoadingStates.toMap(), movies) {
        allImagesLoaded = if (movies.isNotEmpty()) {
            movies.all { movie ->
                val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movie.posterPath}"
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
        if (genresList.isNotEmpty()) {
            GenresListScreen(
                genresList = genresList,
                genreSelected = selectedGenreId,
                onGenreClick = { id ->
                    onGenreClick(id)
                },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        }

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(3)
        ) {
            items(movies.size) { index ->
                val movie = movies[index]
                val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movie.posterPath}"

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
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(2f / 3f)
                        .fillMaxSize()
                        .padding(10.dp)
                        .clickable { onMovieClick(movie.id.toString()) }
                )

                if (index == movies.size - 1 && allImagesLoaded) {
                    onLoadMore()
                }
            }
        }
    }
    DisplayMoviePosition(
        moviesSize = movies.size,
        moviePosition = moviePosition,
        screenMetrics = screenMetrics,
        screenViewModel = screenViewModel
    )
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