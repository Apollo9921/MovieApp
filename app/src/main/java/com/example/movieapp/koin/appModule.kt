package com.example.movieapp.koin

import com.example.movieapp.data.network.instance.MovieInstance
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.presentation.viewModel.MovieDetailsViewModel
import com.example.movieapp.presentation.viewModel.MoviesViewModel
import com.example.movieapp.presentation.viewModel.SearchMoviesViewModel
import com.example.movieapp.domain.repository.ConnectivityObserver
import com.example.movieapp.data.repository.NetworkConnectivityObserver
import com.example.movieapp.domain.usecase.FormatMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import com.example.movieapp.domain.usecase.GetSearchUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatchersModule = module {
    single(named("ioDispatcher")) { Dispatchers.IO }
}

val appModule = module {
    single {
        MovieInstance.api
    }

    single<MoviesRepository> {
        MovieRepositoryImpl(get(), get(named("ioDispatcher")))
    }

    single {
        GetMoviesUseCase(get())
    }

    single {
        GetGenresUseCase(get())
    }

    single {
        GetMovieDetailsUseCase(get())
    }

    single {
        GetSearchUseCase(get())
    }

    single {
        FormatMovieDetailsUseCase()
    }


    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidContext())
    }

    viewModel {
        MoviesViewModel(get(), get(), get())
    }

    viewModel {
        SearchMoviesViewModel(get(), get())
    }

    viewModel {
        MovieDetailsViewModel(get(), get(), get())
    }

}