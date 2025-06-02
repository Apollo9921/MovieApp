package com.example.movieapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.utils.Background
import com.example.movieapp.utils.CircleIndicator
import com.example.movieapp.utils.TopBarBackground
import com.example.movieapp.utils.Typography

@Composable
fun HomeScreen(backStack: NavBackStack?) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { HomeTopBar() },
        content = { MoviesList(it) }
    )
}

@Composable
fun MoviesList(pv: PaddingValues) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(pv)
    ) {
        items(3) {
            val loading = remember { mutableStateOf(false) }
            if (loading.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(75.dp),
                    color = CircleIndicator,
                    strokeWidth = 2.dp
                )
                return@items
            }
            AsyncImage(
                model = "https://cdn.pixabay.com/photo/2024/12/28/03/20/parrot-9295172_1280.jpg",
                onLoading = { loading.value = true },
                onError = { loading.value = false },
                onSuccess = { loading.value = false },
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 75.dp, height = 250.dp)
                    .padding(5.dp)
            )
        }
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
        Text(
            style = Typography.titleLarge,
            text = "Home"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() {
    HomeScreen(null)
}