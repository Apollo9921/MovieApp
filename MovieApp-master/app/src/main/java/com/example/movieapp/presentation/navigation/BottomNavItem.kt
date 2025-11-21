package com.example.movieapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomNavItem(val route: String, @Contextual val icon: ImageVector) : NavKey {
    @Serializable
    data object Home : BottomNavItem("home", Icons.Filled.Home)

    @Serializable
    data object More : BottomNavItem("more", Icons.Filled.Menu)

    @Serializable
    data object Settings : BottomNavItem("settings", Icons.Filled.Settings)
}