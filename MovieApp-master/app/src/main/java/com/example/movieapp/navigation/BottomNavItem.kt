package com.example.movieapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

sealed class BottomNavItem(val route: String,val icon: ImageVector): NavKey {
    data object Home : BottomNavItem("home", Icons.Filled.Home)
    data object More : BottomNavItem("more", Icons.Filled.Menu)
    data object Settings : BottomNavItem("settings", Icons.Filled.Settings)
}