package com.example.movieapp.presentation.screens.details

import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.presentation.components.ErrorScreen
import com.example.movieapp.presentation.components.LoadingScreen
import com.example.movieapp.presentation.components.TopBar
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.theme.Black
import com.example.movieapp.presentation.theme.Typography
import com.example.movieapp.presentation.theme.White
import com.example.movieapp.presentation.viewModel.MovieDetailsViewModel
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsRoute(
    backStack: () -> Boolean,
    movieId: String?,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    viewModel: MovieDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    uiState.movieId = Integer.parseInt(movieId ?: "0")

    DetailsScreen(
        uiState = uiState,
        screenMetrics = screenMetrics,
        screenViewModel = screenViewModel,
        backStack = { backStack() },
        onRefresh = { viewModel.fetchMovieDetails(uiState.movieId) },
        favoritesClick = {
            viewModel.toggleMovie(
                MovieData(
                    id = uiState.movieId,
                    title = uiState.movieDetails?.title ?: "",
                    posterPath = uiState.movieDetails?.posterUrl ?: "",
                    voteAverage = uiState.movieDetailsOriginal?.voteAverage ?: 0.0,
                    voteCount = uiState.movieDetailsOriginal?.voteCount ?: 0,
                    releaseDate = uiState.movieDetails?.releaseYear ?: "",
                    overview = uiState.movieDetails?.overview ?: "",
                    popularity = 0.0,
                    backdropPath = "",
                    genreIds = emptyList(),
                    originalLanguage = "",
                    originalTitle = "",
                    adult = false,
                    video = false
                )
            )
        }
    )
}

@Composable
fun DetailsScreen(
    uiState: MovieDetailsViewModel.MovieDetailsUiState,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel,
    backStack: () -> Boolean,
    onRefresh: () -> Unit,
    favoritesClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                title = stringResource(R.string.details),
                isBack = true,
                backStack = { backStack() },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel,
                hasFavoritesButton = true,
                isFavorite = uiState.isFavorite,
                favoritesClick = { favoritesClick() }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.testTag("LoadingComponent")) {
                        LoadingScreen()
                    }
                }

                uiState.isSuccess -> {
                    Box(modifier = Modifier.testTag("SuccessComponent")) {
                        DetailsContent(
                            uiState = uiState,
                            screenMetrics = screenMetrics,
                            screenViewModel = screenViewModel
                        )
                    }
                }

                uiState.error -> {
                    Box(modifier = Modifier.testTag("ErrorComponent")) {
                        ErrorScreen(
                            errorMessage = uiState.errorMessage,
                            screenMetrics = screenMetrics,
                            screenViewModel = screenViewModel,
                            onRefresh = { onRefresh() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsContent(
    uiState: MovieDetailsViewModel.MovieDetailsUiState,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20, screenMetrics).sp
    val label = screenViewModel.calculateCustomWidth(baseSize = 15, screenMetrics).sp
    val ratingTextSize = screenViewModel.calculateCustomWidth(baseSize = 14, screenMetrics).sp
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("Content")
    ) {
        SectionImage(
            imageUrl = uiState.movieDetails?.posterUrl.toString(),
            scrollState = scrollState,
            screenMetrics = screenMetrics,
            screenViewModel = screenViewModel
        )
        Spacer(modifier = Modifier.height(10.dp))
        SectionDetails(
            title = uiState.movieDetails?.title ?: "",
            voteAverage = uiState.movieDetails?.voteAverage.toString(),
            voteCount = uiState.movieDetails?.voteCount.toString(),
            titleSize = titleSize,
            ratingTextSize = ratingTextSize
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(modifier = Modifier.testTag("SectionReleaseInfo")) {
            SectionReleaseInfo(
                releaseYear = uiState.movieDetails?.releaseYear.toString(),
                genres = uiState.movieDetails?.genres.toString(),
                runtime = uiState.movieDetails?.runtime.toString(),
                labelSize = label
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.testTag("SectionOverview")) {
            if (uiState.movieDetails?.overview.isNullOrEmpty()) return@Column
            SectionTitle("Overview", titleSize)
            Spacer(modifier = Modifier.height(3.dp))
            SectionOverview(
                overview = uiState.movieDetails?.overview ?: "",
                labelSize = label
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.testTag("SectionListLanguages")) {
            SectionList(
                title = "Available Languages",
                titleSize = titleSize,
                list = uiState.movieDetails?.spokenLanguages ?: emptyList(),
                label = label
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.testTag("SectionListCompanies")) {
            SectionList(
                title = "Production Companies",
                titleSize = titleSize,
                list = uiState.movieDetails?.productionCompanies ?: emptyList(),
                label = label
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
private fun SectionImage(
    imageUrl: String,
    scrollState: ScrollState,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val density = LocalDensity.current
    val imageMaxHeight = 300.dp
    val imageMaxHeightPx = with(density) { imageMaxHeight.toPx() }
    val parallaxFactor = 0.5f
    val modifier = if (isLandscape) {
        val landscapeMaxHeight = screenViewModel.calculateCustomWidth(300, screenMetrics).dp
        Modifier
            .fillMaxWidth()
            .height(landscapeMaxHeight)
    } else {
        Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
    }.graphicsLayer {
        translationY = -scrollState.value * parallaxFactor
        val scrollProgress = (scrollState.value / imageMaxHeightPx).coerceIn(0f, 1f)
        alpha = 1f - scrollProgress
    }

    AsyncImage(
        model = imageUrl,
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = painterResource(R.drawable.ic_launcher_background),
        contentDescription = "Movie Poster",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
private fun SectionDetails(
    title: String,
    voteAverage: String,
    voteCount: String,
    titleSize: TextUnit,
    ratingTextSize: TextUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .testTag("SectionDetails"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = Typography.titleLarge.copy(fontSize = titleSize),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(5.dp))
        }
        Box(modifier = Modifier.testTag("SectionRating")) {
            SectionRating(voteAverage, voteCount, ratingTextSize)
        }
    }
}

@Composable
private fun SectionRating(
    voteAverage: String,
    voteCount: String,
    ratingTextSize: TextUnit
) {
    if (voteAverage.isBlank() || voteCount.isBlank()) {
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Rating",
            tint = Color.Yellow,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = "$voteAverage/10 ",
            style = Typography.labelMedium.copy(fontSize = ratingTextSize, color = White)
        )
        Text(
            text = "($voteCount)",
            style = Typography.labelMedium.copy(fontSize = ratingTextSize, color = Color.Gray)
        )
    }
}

@Composable
private fun SectionReleaseInfo(
    releaseYear: String,
    genres: String,
    runtime: String,
    labelSize: TextUnit
) {
    val items = listOf(releaseYear, genres, runtime).filter { it.isNotEmpty() }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            Text(
                text = item,
                style = Typography.labelMedium.copy(fontSize = labelSize)
            )
            if (index < items.size - 1) {
                Text(
                    text = "•",
                    style = Typography.labelMedium.copy(fontSize = labelSize)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, titleSize: TextUnit) {
    Text(
        text = title,
        style = Typography.titleLarge.copy(fontSize = titleSize),
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

@Composable
private fun SectionList(title: String, titleSize: TextUnit, list: List<String>, label: TextUnit) {
    if (list.isEmpty()) return
    SectionTitle(title, titleSize)
    Spacer(modifier = Modifier.height(3.dp))
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        list.forEach { item ->
            if (item.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(5.dp))
                        .background(Black)
                ) {
                    Text(
                        text = item,
                        style = Typography.labelMedium.copy(fontSize = label),
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionOverview(overview: String, labelSize: TextUnit) {
    Text(
        text = overview,
        style = Typography.labelMedium.copy(fontSize = labelSize),
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}