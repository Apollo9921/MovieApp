package com.example.movieapp.utils

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.core.Background
import com.example.movieapp.core.CircleIndicator
import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.MovieData
import com.example.movieapp.networking.model.movies.Movies
import com.example.movieapp.networking.viewModel.MoviesViewModel
import com.example.movieapp.utils.topBar.GenresListScreen

@Composable
fun MoviesList(
    pv: PaddingValues,
    movies: Movies?,
    genresList: GenresList?,
    filteredMovies: List<MovieData>,
    moviesViewModel: MoviesViewModel
) {
    if (movies == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CircleIndicator, strokeWidth = 2.dp)
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(pv)
    ) {
        GenresListScreen(genresList, moviesViewModel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
        ) {
            items(if (filteredMovies.isNotEmpty()) filteredMovies.size else movies.results.size) { index ->
                val movie = if (filteredMovies.isNotEmpty()) filteredMovies[index] else movies.results[index]
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500/${movie.poster_path}",
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(2f / 3f)
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
        }
    }
}