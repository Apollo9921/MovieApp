package com.example.movieapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp, top = 20.dp)
            .height(60.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBar(
            modifier = Modifier
                .width(screenWidthDp)
                .clip(CircleShape),
            containerColor = BottomBarBackground
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 20.dp),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = White,
                        unselectedIconColor = White,
                        indicatorColor = Color.Transparent
                    ),
                    selected = currentRoute == item.route,
                    alwaysShowLabel = false,
                    label = null,
                    onClick = {
                       /* navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }*/
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.route
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = NavController(LocalContext.current))
}