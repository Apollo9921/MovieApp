package com.example.movieapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.screens.HomeScreen
import com.example.movieapp.screens.MoreScreen
import com.example.movieapp.screens.SettingsScreen
import com.example.movieapp.screens.more.SearchScreen

@Composable
fun BasicNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = BottomNavItem.More.route) {
            MoreScreen(navController = navController)
        }
        composable(route = BottomNavItem.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(route = Screen.Search.route) {
            SearchScreen(
                navController = navController,
                backStack = navController::popBackStack
            )
        }

    }
}