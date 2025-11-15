package com.example.movieapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.presentation.screens.details.DetailsScreen
import com.example.movieapp.presentation.screens.HomeScreen
import com.example.movieapp.presentation.screens.MoreScreen
import com.example.movieapp.presentation.screens.SettingsScreen
import com.example.movieapp.presentation.screens.more.SearchScreen
import com.example.movieapp.core.utils.size.getScreenMetrics
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

private var screenViewModel: ScreenSizingViewModel = ScreenSizingViewModel()

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()
    val screenMetrics = getScreenMetrics()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                navController = navController,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        }
        composable(route = BottomNavItem.More.route) {
            MoreScreen(
                navController = navController,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        }
        composable(route = BottomNavItem.Settings.route) {
            SettingsScreen(
                navController = navController,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        }
        composable(route = Screen.Search.route) {
            SearchScreen(
                navController = navController,
                backStack = navController::navigateUp,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        }
        composable(route = Screen.Details.route) {
            val movieId = navController.currentBackStackEntry?.arguments?.getString("movieId")
            DetailsScreen(
                movieId = movieId,
                navController = navController,
                backStack = navController::navigateUp,
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        }
    }
}