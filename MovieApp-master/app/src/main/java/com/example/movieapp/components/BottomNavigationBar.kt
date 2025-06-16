package com.example.movieapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.movieapp.core.BottomBarBackground
import com.example.movieapp.core.White
import com.example.movieapp.navigation.BottomNavItem
import com.example.movieapp.utils.size.ScreenSizeUtils

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.More,
        BottomNavItem.Settings
    )

    val screenWidthDp = ScreenSizeUtils.getScreenWidthDp() / 2
    val bottomBarHeight = 60.dp
    val bottomSize = ScreenSizeUtils.calculateCustomHeight(baseSize = 50).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = bottomSize, top = 10.dp)
            .height(bottomBarHeight),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(screenWidthDp)
                .clip(CircleShape)
                .background(BottomBarBackground),
            contentAlignment = Alignment.BottomCenter
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.route,
                        tint = if (currentRoute == item.route) White else Color.Gray,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                if (currentRoute != item.route) {
                                   navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = NavController(LocalContext.current))
}