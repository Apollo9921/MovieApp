package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.components.ErrorScreen
import com.example.movieapp.core.Background
import com.example.movieapp.core.Black
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.core.White
import com.example.movieapp.networking.instance.MovieInstance
import com.example.movieapp.networking.model.details.MovieDetails
import com.example.movieapp.networking.viewModel.MovieDetailsViewModel
import com.example.movieapp.utils.network.ConnectivityObserver
import com.example.movieapp.utils.size.ScreenSizeUtils
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat

private var viewModel: MovieDetailsViewModel? = null

@Composable
fun DetailsScreen(navController: NavHostController, backStack: () -> Boolean, movieId: String?) {
    viewModel = koinViewModel<MovieDetailsViewModel>()

    val isLoading = viewModel?.isLoading?.value
    val isSuccess = viewModel?.isSuccess?.value
    val isError = viewModel?.isError?.value
    val errorMessage = viewModel?.errorMessage?.value
    val networkStatus = viewModel?.networkStatus?.collectAsState()

    val movieDetails = viewModel?.movieDetails
    var isConnected = remember { mutableStateOf(false) }
    if (networkStatus?.value == ConnectivityObserver.Status.Available && !isConnected.value) {
        isConnected.value = true
        viewModel?.fetchMovieDetails(movieId!!.toInt())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = { DetailsTopBar(backStack) },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
            ) {
                when {
                    isLoading == true -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Background),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                        }
                    }

                    isSuccess == true -> {
                        if (movieDetails == null) return@Box
                        DetailsContent(it, movieDetails)
                    }

                    isError == true -> {
                        ErrorScreen(errorMessage)
                    }

                    networkStatus?.value == ConnectivityObserver.Status.Unavailable -> {
                        ErrorScreen(stringResource(R.string.no_internet_connection))
                    }
                }
            }
        }
    )
}

@Composable
private fun DetailsContent(pv: PaddingValues, movieDetails: MovieDetails) {
    val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
    val label = ScreenSizeUtils.calculateCustomWidth(baseSize = 15).sp
    val ratingTextSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 14).sp
    val imageUrl = "${MovieInstance.BASE_URL_IMAGE}${movieDetails.poster_path}"
    val formattedVoteAverage = DecimalFormat("#.#").format(movieDetails.vote_average)
    val releaseDate = movieDetails.release_date.split("-").first()
    val genres = movieDetails.genres.joinToString(", ") { it.name }
    val hours = movieDetails.runtime / 60
    val minutes = movieDetails.runtime % 60
    val runtime = "${hours}h ${minutes}m"
    val languages = movieDetails.spoken_languages.map { it.name }
    val companies = movieDetails.production_companies.map { it.name }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(Background)
            .padding(pv),
    ) {
        SectionImage(imageUrl)
        Spacer(modifier = Modifier.padding(10.dp))
        SectionDetails(movieDetails, titleSize, ratingTextSize, formattedVoteAverage)
        Spacer(modifier = Modifier.padding(3.dp))
        SectionReleaseDate(releaseDate, genres, runtime, label)
        Spacer(modifier = Modifier.padding(10.dp))
        SectionTitle("Available Languages", titleSize)
        Spacer(modifier = Modifier.padding(3.dp))
        SectionList(languages, label)
        Spacer(modifier = Modifier.padding(10.dp))
        SectionTitle("Overview", titleSize)
        Spacer(modifier = Modifier.padding(3.dp))
        SectionOverview(movieDetails, label)
        Spacer(modifier = Modifier.padding(10.dp))
        SectionTitle("Production Companies", titleSize)
        Spacer(modifier = Modifier.padding(3.dp))
        SectionList(companies, label)
    }
}

@Composable
private fun SectionImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = painterResource(R.drawable.ic_launcher_background),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
    )
}

@Composable
private fun SectionDetails(
    movieDetails: MovieDetails,
    titleSize: TextUnit,
    ratingTextSize: TextUnit,
    formattedVoteAverage: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = movieDetails.title,
            style = Typography.titleLarge.copy(fontSize = titleSize),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.padding(5.dp))
        SectionRating(formattedVoteAverage, ratingTextSize, movieDetails)
    }
}

@Composable
private fun SectionRating(
    formattedVoteAverage: String,
    ratingTextSize: TextUnit,
    movieDetails: MovieDetails
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Color.Yellow,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = "$formattedVoteAverage/10 ",
            style = Typography.labelMedium.copy(
                fontSize = ratingTextSize,
                color = White
            )
        )
        Text(
            text = "(${movieDetails.vote_count})",
            style = Typography.labelMedium.copy(
                fontSize = ratingTextSize,
                color = Color.Gray
            )
        )
    }
}

@Composable
private fun SectionReleaseDate(
    releaseDate: String,
    genres: String,
    runtime: String,
    label: TextUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = releaseDate,
            style = Typography.labelMedium.copy(fontSize = label)
        )
        Spacer(modifier = Modifier.padding(3.dp))
        Text(
            text = "•",
            style = Typography.labelMedium.copy(fontSize = label)
        )
        Spacer(modifier = Modifier.padding(3.dp))
        Text(
            text = genres,
            style = Typography.labelMedium.copy(fontSize = label)
        )
        Spacer(modifier = Modifier.padding(3.dp))
        Text(
            text = "•",
            style = Typography.labelMedium.copy(fontSize = label)
        )
        Spacer(modifier = Modifier.padding(3.dp))
        Text(
            text = runtime,
            style = Typography.labelMedium.copy(fontSize = label)
        )
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
private fun SectionList(list: List<String>, label: TextUnit) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        list.forEach { language ->
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Black)
            ) {
                Text(
                    text = language,
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

@Composable
private fun SectionOverview(movieDetails: MovieDetails, label: TextUnit) {
    Text(
        text = movieDetails.overview,
        style = Typography.labelMedium.copy(fontSize = label),
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

@Composable
private fun DetailsTopBar(backStack: () -> Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarBackground)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier.clickable { backStack() }
        )
        Spacer(modifier = Modifier.padding(10.dp))
        val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = stringResource(R.string.details)
        )
    }
}