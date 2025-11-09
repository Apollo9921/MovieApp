package com.example.movieapp.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.components.ErrorScreen
import com.example.movieapp.components.MoviesList
import com.example.movieapp.components.TopBar
import com.example.movieapp.core.Background
import com.example.movieapp.core.White
import com.example.movieapp.viewModel.ScreenSizingViewModel
import com.example.movieapp.viewModel.SearchMoviesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private var viewModel: SearchMoviesViewModel? = null

@Composable
fun SearchScreen(
    navController: NavController,
    backStack: () -> Boolean,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
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
        topBar = {
            TopBar(
                stringResource(R.string.search),
                isBack = true,
                backStack = { backStack() },
                screenMetrics = screenMetrics,
                screenViewModel = screenViewModel
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(it)
            ) {
                SearchBar(screenMetrics, screenViewModel)
                when {
                    isLoading == true || isSuccess == true -> {
                        if (isSuccess == true && searchMovies.isEmpty()) {
                            ErrorScreen(
                                stringResource(R.string.no_movies_found),
                                screenMetrics,
                                screenViewModel
                            )
                            return@Column
                        }
                        MoviesList(
                            PaddingValues(0.dp),
                            searchMovies,
                            arrayListOf(),
                            emptyList(),
                            0,
                            viewModel!!,
                            navController,
                            screenMetrics,
                            screenViewModel
                        )
                    }
                    isError == true -> {
                        ErrorScreen(errorMessage, screenMetrics, screenViewModel)
                    }
                }
            }
        }
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun SearchBar(
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
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
            modifier = Modifier.width(screenViewModel.calculateCustomWidth(baseSize = 250, screenMetrics).dp)
        )
    }
}