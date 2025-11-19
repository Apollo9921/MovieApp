package com.example.movieapp.presentation.screens.more

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.R
import com.example.movieapp.presentation.components.ErrorScreen
import com.example.movieapp.presentation.components.LoadingScreen
import com.example.movieapp.presentation.components.MoviesList
import com.example.movieapp.presentation.components.TopBar
import com.example.movieapp.presentation.theme.Background
import com.example.movieapp.presentation.theme.White
import com.example.movieapp.presentation.viewModel.ScreenSizingViewModel
import com.example.movieapp.presentation.viewModel.SearchMoviesViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    backStack: () -> Boolean,
    screenMetrics: ScreenSizingViewModel.ScreenMetrics,
    screenViewModel: ScreenSizingViewModel
) {
    val viewModel = koinViewModel<SearchMoviesViewModel>()
    val uiState = viewModel.uiState.collectAsState()

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
                SearchBar(screenMetrics, screenViewModel, viewModel)
                when {
                    uiState.value.isLoading == true -> {
                        LoadingScreen()
                    }

                    uiState.value.isSuccess == true -> {
                        MoviesList(
                            PaddingValues(0.dp),
                            uiState.value.moviesList,
                            arrayListOf(),
                            emptyList(),
                            0,
                            viewModel,
                            navController,
                            screenMetrics,
                            screenViewModel
                        )
                    }

                    uiState.value.isError == true -> {
                        ErrorScreen(uiState.value.errorMessage, screenMetrics, screenViewModel)
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
    screenViewModel: ScreenSizingViewModel,
    viewModel: SearchMoviesViewModel
) {
    val searchValue = remember { mutableStateOf("") }
    LaunchedEffect(searchValue.value) {
        snapshotFlow { searchValue.value }
            .debounce(500L)
            .distinctUntilChanged { old, new ->
                old == new
            }
            .collectLatest { query ->
                viewModel.onQueryChanged(query)
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
            modifier = Modifier.width(
                screenViewModel.calculateCustomWidth(
                    baseSize = 250,
                    screenMetrics
                ).dp
            )
        )
    }
}