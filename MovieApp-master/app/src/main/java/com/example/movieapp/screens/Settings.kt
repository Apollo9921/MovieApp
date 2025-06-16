package com.example.movieapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.movieapp.utils.size.ScreenSizeUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingsTopBar() },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            SettingsOptions(it, navController)
        }
    )
}

@Composable
private fun SettingsOptions(pv: PaddingValues, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(pv),
        contentAlignment = Alignment.Center
    ) {
        //TODO create settings options
    }
}

@Composable
private fun SettingsTopBar() {
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
            text = stringResource(R.string.settings)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    val navController = NavController(LocalContext.current)
    SettingsScreen(navController)
}