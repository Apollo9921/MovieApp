package com.example.movieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapp.R
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.core.White
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel

@Composable
fun TopBar(
    title: String,
    isBack: Boolean = true,
    backStack: () -> Boolean,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    if (isBack) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TopBarBackground)
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null,
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier.clickable { backStack() }
            )
            Spacer(modifier = Modifier.padding(10.dp))
            val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20, screenMetrics).sp
            Text(
                style = Typography.titleLarge.copy(fontSize = titleSize),
                text = title
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TopBarBackground)
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val titleSize = screenViewModel.calculateCustomWidth(baseSize = 20, screenMetrics).sp
            Text(
                style = Typography.titleLarge.copy(fontSize = titleSize),
                text = title
            )
        }
    }
}