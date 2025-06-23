package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.core.Background
import com.example.movieapp.core.Black
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.core.White
import com.example.movieapp.utils.size.ScreenSizeUtils
import java.text.DecimalFormat

@Composable
fun DetailsScreen(navController: NavHostController, backStack: () -> Boolean) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = { DetailsTopBar(backStack) },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            DetailsContent(it, 8.113, 6784)
        }
    )
}

@Composable
private fun DetailsContent(pv: PaddingValues, voteAverage: Double, voteCount: Int) {
    val imageHeight = ScreenSizeUtils.calculateCustomHeight(baseSize = 300).dp
    val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
    val label = ScreenSizeUtils.calculateCustomWidth(baseSize = 15).sp
    val ratingTextSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 14).sp

    val formattedVoteAverage = DecimalFormat("#.#").format(voteAverage)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(pv),
    ) {
        item {
            AsyncImage(
                model = "",
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                onError = { state ->

                },
                onSuccess = { state ->

                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Title Movie",
                    style = Typography.titleLarge.copy(fontSize = titleSize),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
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
                        text = "($voteCount)",
                        style = Typography.labelMedium.copy(
                            fontSize = ratingTextSize,
                            color = Color.Gray
                        ) // Lighter color for vote count
                    )
                }
            }
            Spacer(modifier = Modifier.padding(3.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2023",
                    style = Typography.labelMedium.copy(fontSize = label)
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = "•",
                    style = Typography.labelMedium.copy(fontSize = label)
                )
                Spacer(modifier = Modifier.padding(3.dp))
                val genres = arrayListOf("Action", "Adventure", "Fantasy")
                Text(
                    text = genres.joinToString(", "),
                    style = Typography.labelMedium.copy(fontSize = label)
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = "•",
                    style = Typography.labelMedium.copy(fontSize = label)
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = "1h 30m",
                    style = Typography.labelMedium.copy(fontSize = label)
                )
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Available Languages",
                style = Typography.titleLarge.copy(fontSize = titleSize),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            val languages = arrayListOf("English", "Portuguese", "Spanish")
            Spacer(modifier = Modifier.padding(3.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                languages.forEach { language ->
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Black)
                            .padding(horizontal = 3.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = language,
                            style = Typography.labelMedium.copy(fontSize = label),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.padding(3.dp))
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Overview",
                style = Typography.titleLarge.copy(fontSize = titleSize),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.padding(3.dp))
            Text(
                text = "A card shark and his unwillingly-enlisted friends need to make a lot of cash quick after losing a sketchy poker match. To do this they decide to pull a heist on a small-time gang who happen to be operating out of the flat next door.",
                style = Typography.labelMedium.copy(fontSize = label),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Production Companies",
                style = Typography.titleLarge.copy(fontSize = titleSize),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            val companies = arrayListOf("Warner Bros.", "Disney", "Pixar")
            Spacer(modifier = Modifier.padding(3.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                companies.forEach { company ->
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Black)
                            .padding(horizontal = 3.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = company,
                            style = Typography.labelMedium.copy(fontSize = label),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.padding(3.dp))
                }
            }
        }
    }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    DetailsScreen(navController, navController::popBackStack)
}