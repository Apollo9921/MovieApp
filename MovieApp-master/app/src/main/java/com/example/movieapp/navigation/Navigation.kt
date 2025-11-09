package com.example.movieapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.screens.details.DetailsScreen
import com.example.movieapp.screens.HomeScreen
import com.example.movieapp.screens.MoreScreen
import com.example.movieapp.screens.SettingsScreen
import com.example.movieapp.screens.more.SearchScreen
import com.example.movieapp.utils.size.getScreenMetrics
import com.example.movieapp.viewModel.ScreenSizingViewModel

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