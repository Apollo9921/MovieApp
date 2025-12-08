package com.example.movieapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.presentation.theme.Black
import com.example.movieapp.presentation.theme.Selected
import com.example.movieapp.presentation.theme.TopBarBackground
import com.example.movieapp.presentation.theme.Typography
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun GenresListScreen(
    genresList: List<Genre> = emptyList(),
    genreSelected: Int,
    onGenreClick: (Int) -> Unit,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        items(items = genresList, key = { it.id }) { genre ->
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 10.dp)
                    .clickable {
                        onGenreClick(genre.id)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (genreSelected == genre.id) Selected else TopBarBackground,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                border = BorderStroke(2.dp, Black)
            ) {
                val label = screenViewModel.calculateCustomWidth(baseSize = 15, screenMetrics).sp
                Text(
                    style = Typography.labelMedium.copy(fontSize = label),
                    text = genre.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(7.dp)
                )
            }
        }
    }
}