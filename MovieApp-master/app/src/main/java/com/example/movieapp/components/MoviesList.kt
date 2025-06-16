package com.example.movieapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.movieapp.core.Background
import com.example.movieapp.networking.model.genres.Genre
import com.example.movieapp.networking.model.movies.MovieData
import com.example.movieapp.networking.viewModel.MoviesViewModel
import com.example.movieapp.core.White
import com.example.movieapp.networking.instance.MovieInstance

@Composable
fun MoviesList(
    pv: PaddingValues,
    movies: ArrayList<MovieData>,
    genresList: ArrayList<Genre>?,
    filteredMovies: List<MovieData>,
    genreSelected: Int,
    moviesViewModel: MoviesViewModel
) {
    if (movies.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = White, strokeWidth = 2.dp)
        }
        return
    }

    val imageLoadingStates = remember { mutableStateMapOf<String, AsyncImagePainter.State>() }
    var allImagesLoaded by remember { mutableStateOf(false) }
    val currentMovies = if (filteredMovies.isNotEmpty() || genreSelected != 0) filteredMovies else movies

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
        GenresListScreen(genresList, genreSelected, moviesViewModel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
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
                )

                if (filteredMovies.isEmpty()) {
                    if (index == movies.size - 1 && allImagesLoaded) {
                        moviesViewModel.fetchMovies()
                    }
                }
            }
        }
    }
}