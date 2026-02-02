package com.example.movieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.movieapp.R
import com.example.movieapp.presentation.theme.toTextColor
import com.example.movieapp.presentation.utils.TopBarAction

@Composable
fun TopBar(
    title: String,
    isBack: Boolean = true,
    backStack: () -> Unit,
    action: TopBarAction = TopBarAction.None
) {
    val fontScale = MaterialTheme.typography.titleLarge.fontSize.value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isBack) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size((fontScale).dp)
                        .clickable { backStack() }
                )
                Spacer(modifier = Modifier.padding(10.dp))
            }
            Text(
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.toTextColor(),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        RenderAction(action)
    }
}

@Composable
private fun RenderAction(action: TopBarAction) {
    val fontScale = MaterialTheme.typography.titleLarge.fontSize.value
    when (action) {
        is TopBarAction.Details -> {
            Image(
                painter = painterResource(
                    id = if (action.isFavorite) R.drawable.favorite else R.drawable.favorite_border
                ),
                contentDescription = "Favorite",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onError),
                modifier = Modifier
                    .size((fontScale).dp)
                    .clickable { action.onClick() }
            )
        }
        is TopBarAction.Favorite -> {
            Image(
                painter = painterResource(id = action.iconRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size((fontScale).dp)
                    .clickable { action.onClick() }
            )
        }
        TopBarAction.None -> {  }
    }
}