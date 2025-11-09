package com.example.movieapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.components.TopBar
import com.example.movieapp.core.Background
import com.example.movieapp.viewModel.ScreenSizingViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
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
                stringResource(R.string.settings),
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