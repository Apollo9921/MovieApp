package com.example.movieapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.movieapp.screens.DetailsScreen
import com.example.movieapp.screens.HomeScreen

@Composable
fun BasicNavigation() {
    val backStack = rememberNavBackStack<Screen>(Screen.Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Home> {
                HomeScreen(backStack)
            }
            entry<Screen.Details> { key ->
                DetailsScreen(key)
            }
        }
    )
}