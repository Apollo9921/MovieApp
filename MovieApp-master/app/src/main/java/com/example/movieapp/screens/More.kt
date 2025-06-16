package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.core.Background
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.core.White
import com.example.movieapp.utils.size.ScreenSizeUtils

@Composable
fun MoreScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MoreTopBar() },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            MoreScreenOptions(it, navController)
        }
    )
}

@Composable
private fun MoreScreenOptions(pv: PaddingValues, navController: NavController) {
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
                        //navController.navigate(option[it].second)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val iconSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 100).dp
                Image(
                    painter = painterResource(id = option[it].first),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
                Text(
                    style = Typography.displayMedium.copy(fontSize = titleSize),
                    text = option[it].second
                )
            }
        }
    }
}

@Composable
private fun MoreTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarBackground)
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .safeDrawingPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = stringResource(R.string.more)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MoreScreenPreview() {
    val navController = NavController(LocalContext.current)
    MoreScreen(navController)
}