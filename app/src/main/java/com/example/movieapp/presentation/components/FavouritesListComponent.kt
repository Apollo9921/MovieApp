package com.example.movieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.data.network.instance.MovieInstance
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.theme.toTextColor
import com.example.movieapp.presentation.utils.rememberDragDropState
import com.example.movieapp.presentation.viewModel.FavoritesViewModel
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun FavouritesListComponent(
    movieData: List<MovieData>,
    genreTypeSelected: FavoritesViewModel.GenresState,
    filterMovies: List<MovieData>,
    genresList: List<Genre>,
    onGenreClick: (Int) -> Unit,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    onMove: (Int, Int) -> Unit,
    updateMoviePosition: () -> Unit,
    isDraggingEnabled: Boolean,
    goToDetails: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val dragDropState = rememberDragDropState(lazyListState) { from, to ->
        onMove(from, to)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(horizontal = 5.dp)
    ) {
        if (genresList.isNotEmpty() && !isDraggingEnabled) {
            GenresListScreen(
                genresList = genresList,
                genreSelected = genreTypeSelected.genresType,
                onGenreClick = { id ->
                    onGenreClick(id)
                }
            )
            Spacer(Modifier.padding(5.dp))
        }
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dragDropState) {
                    if (!isDraggingEnabled) return@pointerInput
                    detectDragGesturesAfterLongPress(
                        onDragStart = { offset -> dragDropState.onDragStart(offset) },
                        onDragEnd = {
                            dragDropState.onDragInterrupted()
                            updateMoviePosition()
                        },
                        onDragCancel = { dragDropState.onDragInterrupted() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragDropState.onDrag(dragAmount)
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val itemToShow =
                if (filterMovies.isNotEmpty() && !isDraggingEnabled || genreTypeSelected.genresType != 0 && !isDraggingEnabled)
                    filterMovies
                else
                    movieData
            itemsIndexed(
                items = itemToShow,
                key = { _, item -> item.id }
            ) { index, movie ->
                val dragging = movie.id == dragDropState.draggingItemKey
                val itemModifier = if (dragging) {
                    Modifier
                        .zIndex(1f)
                        .graphicsLayer {
                            translationY = dragDropState.draggingItemOffset
                            alpha = 0.8f
                        }
                } else {
                    Modifier.zIndex(0f)
                }

                FavouritesListItem(
                    modifier = itemModifier,
                    movie = movie,
                    screenMetrics = screenMetrics,
                    isDraggingEnabled = isDraggingEnabled,
                    goToDetails = goToDetails,
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
    screenViewModel: ScreenSizingViewModel,
    isDraggingEnabled: Boolean,
    goToDetails: (String) -> Unit,
    modifier: Modifier
) {
    val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movie.posterPath}"
    val baseFontSize = 20f
    val currentFontSize = MaterialTheme.typography.titleLarge.fontSize.value
    val fontScaleMultiplier = currentFontSize / baseFontSize
    val iconSize = screenViewModel.calculateCustomWidth(baseSize = 30, screenMetrics).dp
    val imageSizeWidth = (screenViewModel.calculateCustomWidth(100, screenMetrics) * fontScaleMultiplier).dp
    val imageSizeHeight = (screenViewModel.calculateCustomWidth(150, screenMetrics) * fontScaleMultiplier).dp


    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (!isDraggingEnabled) {
                    goToDetails(movie.id.toString())
                }
            },
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
                .height(imageSizeHeight)
        )
        Spacer(Modifier.padding(10.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = movie.title,
                color = MaterialTheme.toTextColor()
            )
            Spacer(Modifier.padding(5.dp))
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = movie.overview,
                color = MaterialTheme.toTextColor(),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.padding(5.dp))
        if (isDraggingEnabled) {
            Image(
                painter = painterResource(id = R.drawable.drag),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(0.4f)
                    .size(iconSize)
                    .testTag("DragComponent")
            )
        }
    }
}