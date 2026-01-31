package com.example.movieapp.presentation.viewModel

import android.util.Log
import com.example.movieapp.core.Constants
import com.example.movieapp.domain.model.genres.Genre
import com.example.movieapp.domain.model.genres.GenresList
import com.example.movieapp.domain.model.movies.MovieData
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.domain.usecase.GetFavoriteMoviesUseCase
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.UpdateFavoritesMoviesPositionUseCase
import com.example.movieapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getFavoriteMoviesUseCase = mockk<GetFavoriteMoviesUseCase>()
    private val updateFavoritesMoviesPositionUseCase = mockk<UpdateFavoritesMoviesPositionUseCase>()
    private val getGenresUseCase = mockk<GetGenresUseCase>()
    private val connectivityObserver = mockk<ConnectivityObserver>()

    private lateinit var viewModel: FavoritesViewModel

    private val fakeMovies = listOf(
        MovieData(
            id = 1,
            title = "Matrix",
            posterPath = "",
            voteAverage = 9.0,
            voteCount = 0,
            releaseDate = "",
            overview = "",
            popularity = 0.0,
            backdropPath = "",
            genreIds = emptyList(),
            originalLanguage = "",
            originalTitle = "",
            adult = false,
            video = false,
            page = 1
        ),
        MovieData(
            id = 2,
            title = "Inception",
            posterPath = "",
            voteAverage = 8.8,
            voteCount = 1,
            releaseDate = "",
            overview = "",
            popularity = 0.0,
            backdropPath = "",
            genreIds = emptyList(),
            originalLanguage = "",
            originalTitle = "",
            adult = false,
            video = false,
            page = 1
        )
    )
    private val fakeGenres = GenresList(listOf(Genre(1, "Adventure")))

    private fun callViewModel() {
        viewModel = FavoritesViewModel(
            getFavoriteMoviesUseCase,
            updateFavoritesMoviesPositionUseCase,
            getGenresUseCase,
            connectivityObserver
        )
    }

    @Before
    fun setup() {
        mockkStatic(Log::class)
        // ignore the logs
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        // simulate the connectivity status is available
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
    }

    @Test
    fun `load favorite movies successfully`() = runTest {
        // --- ARRANGE ---
        coEvery { getFavoriteMoviesUseCase.invoke() } returns flowOf(Result.success(fakeMovies))
        coEvery { getGenresUseCase.invoke() } returns flowOf(Result.success(fakeGenres))

        // --- ACT ---
        callViewModel()
        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // error should be false
        assertFalse("The state should not be error", state.isError)
        // errorMessage should be null
        assertEquals(null, state.errorMessage)
        // isSuccess should be true
        assertEquals(true, state.isSuccess)
        // moviesList should have the same content as fake movies
        assertEquals(fakeMovies.size, state.moviesList.size)
        assertEquals(fakeMovies[0].id, state.moviesList[0].id)
        assertEquals(fakeMovies[1].id, state.moviesList[1].id)
    }

    @Test
    fun `load favorite movies failure`() = runTest {
        // --- ARRANGE ---
        val errorMessage = RuntimeException("Error loading movies")
        coEvery { getFavoriteMoviesUseCase.invoke() } returns flowOf(Result.failure(errorMessage))
        coEvery { getGenresUseCase.invoke() } returns flowOf(Result.success(GenresList(listOf(Genre(0, "Adventure")))))

        callViewModel()

        // --- ACT ---
        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // error should be true
        assertEquals(true, state.isError)
        // errorMessage should not be null
        assertEquals(errorMessage.message, state.errorMessage)
        // isSuccess should be false
        assertEquals(false, state.isSuccess)
    }

    @Test
    fun `load favorites movies but data is empty`() = runTest {
        // --- ARRANGE ---
        coEvery { getFavoriteMoviesUseCase.invoke() } returns flowOf(Result.success(emptyList()))
        coEvery { getGenresUseCase.invoke() } returns flowOf(Result.success(fakeGenres))

        callViewModel()

        // --- ACT ---
        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        // loading should be false
        assertFalse("The state should not be loading", state.isLoading)
        // isSuccess should be false as the call was not successful
        assertEquals(false, state.isSuccess)
        // error should be true
        assertTrue("The state should be error", state.isError)
        // The movies list should be empty
        assertEquals(true, state.moviesList.isEmpty())
        assertEquals(Constants.NO_MOVIES_FOUND, state.errorMessage)
    }


    @Test
    fun `move movie to another position`() = runTest {
        // --- ARRANGE ---
        coEvery { getFavoriteMoviesUseCase.invoke() } returns flowOf(Result.success(fakeMovies))
        coEvery { getGenresUseCase.invoke() } returns flowOf(Result.success(fakeGenres))
        coEvery { updateFavoritesMoviesPositionUseCase.invoke(any()) } returns flowOf(Result.success(Unit))

        callViewModel()

        // --- ACT ---
        advanceUntilIdle()
        val from = 1
        val to = 0
        viewModel.moveMovie(from, to)
        advanceUntilIdle()

        // --- ASSERT ---
        val state = viewModel.uiState.value
        assertEquals(0, state.moviesList[to].voteCount)
        assertEquals(1, state.moviesList[from].voteCount)
        assertEquals("Matrix", state.moviesList[from].title)
        assertEquals("Inception", state.moviesList[to].title)
    }
}