package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.components.TopBar
import com.example.movieapp.core.Background
import com.example.movieapp.core.Typography
import com.example.movieapp.core.White
import com.example.movieapp.viewModel.ScreenSizingViewModel

@Composable
fun MoreScreen(
    navController: NavController,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                stringResource(R.string.more),
                isBack = false,
                backStack = { false },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        },
        bottomBar = { BottomNavigationBar(
            navController = navController,
            screenMetrics = screenMetrics,
            screenViewModel = screenViewModel
        ) },
        content = {
            MoreScreenOptions(it, navController, screenMetrics, screenViewModel)
        }
    )
}

@Composable
private fun MoreScreenOptions(
    pv: PaddingValues,
    navController: NavController,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val option: ArrayList<Pair<Int, String>> = arrayListOf()
    option.add(Pair(R.drawable.search, stringResource(R.string.search)))
    option.add(Pair(R.drawable.favourite, stringResource(R.string.favourites)))

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentPadding = pv,
        columns = GridCells.Fixed(2)
    ) {
        items(count = option.size) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.dp)
                    .clickable {
                        // TODO Handle option click
                        navController.navigate(option[0].second)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val iconSize = screenViewModel.calculateCustomWidth(baseSize = 75, screenMetrics).dp
                Image(
                    painter = painterResource(id = option[it].first),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20, screenMetrics).sp
                Text(
                    style = Typography.displayMedium.copy(fontSize = titleSize),
                    text = option[it].second
                )
            }
        }
    }
}