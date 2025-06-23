package com.example.movieapp.screens.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.BottomNavigationBar
import com.example.movieapp.components.ErrorScreen
import com.example.movieapp.components.MoviesList
import com.example.movieapp.core.Background
import com.example.movieapp.core.TopBarBackground
import com.example.movieapp.core.Typography
import com.example.movieapp.core.White
import com.example.movieapp.networking.viewModel.SearchMoviesViewModel
import com.example.movieapp.utils.size.ScreenSizeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private var viewModel: SearchMoviesViewModel? = null

@Composable
fun SearchScreen(navController: NavController, backStack: () -> Boolean) {
    viewModel = koinViewModel<SearchMoviesViewModel>()
    val isLoading = viewModel?.isLoading?.value
    val isSuccess = viewModel?.isSuccess?.value
    val isError = viewModel?.isError?.value

    val errorMessage = viewModel?.errorMessage?.value
    val searchMovies = viewModel?.moviesList ?: ArrayList()


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = { SearchTopBar(backStack) },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(it)
            ) {
                SearchBar()
                when {
                    isLoading == true || isSuccess == true -> {
                        if (isSuccess == true && searchMovies.isEmpty()) {
                            ErrorScreen(stringResource(R.string.no_movies_found))
                            return@Column
                        }
                        MoviesList(
                            PaddingValues(0.dp),
                            searchMovies,
                            arrayListOf(),
                            emptyList(),
                            0,
                            viewModel!!,
                            navController
                        )
                    }
                    isError == true -> {
                        ErrorScreen(errorMessage)
                    }
                }
            }
        }
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun SearchBar() {
    val searchValue = remember { mutableStateOf("") }
    LaunchedEffect(searchValue.value) {
        snapshotFlow { searchValue.value }
            .debounce(500L)
            .collectLatest { query ->
                if (query.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel?.searchMovies(query)
                    }
                }
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = searchValue.value,
            onValueChange = { searchValue.value = it },
            label = { Text(text = stringResource(R.string.search_hint)) },
            shape = RoundedCornerShape(20.dp),
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = White,
                focusedIndicatorColor = Background,
                unfocusedIndicatorColor = Background,
            ),
            modifier = Modifier.width(ScreenSizeUtils.calculateCustomWidth(baseSize = 250).dp)
        )
    }
}

@Composable
private fun SearchTopBar(backStack: () -> Boolean) {
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
        val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = stringResource(R.string.search)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SearchScreenPreview() {
    val navController = NavController(LocalContext.current)
    SearchScreen(navController, navController::popBackStack)
}