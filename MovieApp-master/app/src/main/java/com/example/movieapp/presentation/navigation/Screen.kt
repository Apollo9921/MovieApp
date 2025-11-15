package com.example.movieapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object Details : Screen("details_screen/{movieId}")
    data object Search : Screen("search")
}