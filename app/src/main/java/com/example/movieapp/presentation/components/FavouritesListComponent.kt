package com.example.movieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.data.network.instance.MovieInstance
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.theme.Typography
import com.example.movieapp.presentation.theme.White
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun FavouritesListComponent(
    movieData: List<MovieData>,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(movieData.size) {
                FavouritesListItem(
                    movie = movieData[it],
                    screenMetrics = screenMetrics,
                    screenViewModel = screenViewModel
                )
                Spacer(Modifier.padding(5.dp))
            }
        }
    }
}

@Composable
private fun FavouritesListItem(
    movie: MovieData,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movie.posterPath}"
    val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20, screenMetrics).sp
    val overviewSize = screenViewModel.calculateCustomWidth(baseSize = 14, screenMetrics).sp
    val imageSizeWidth = screenViewModel.calculateCustomWidth(baseSize = 100, screenMetrics).dp
    val imageSizeHeight = screenViewModel.calculateCustomWidth(baseSize = 150, screenMetrics).dp
    val iconSize = screenViewModel.calculateCustomWidth(baseSize = 30, screenMetrics).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = imageUrl,
            placeholder = painterResource(R.drawable.ic_launcher_background),
            error = painterResource(R.drawable.ic_launcher_background),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(imageSizeWidth)
                .size(imageSizeHeight)
        )
        Spacer(Modifier.padding(10.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                style = Typography.titleLarge.copy(fontSize = titleSize),
                text = movie.title
            )
            Spacer(Modifier.padding(5.dp))
            Text(
                style = Typography.labelMedium.copy(fontSize = overviewSize),
                text = movie.overview,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.padding(5.dp))
        Image(
            painter = painterResource(id = R.drawable.drag),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier
                .weight(0.4f)
                .size(iconSize)
        )
    }
}