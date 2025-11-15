package com.example.movieapp.koin

import com.example.movieapp.data.network.instance.MovieInstance
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.presentation.viewModel.MovieDetailsViewModel
import com.example.movieapp.presentation.viewModel.MoviesViewModel
import com.example.movieapp.presentation.viewModel.SearchMoviesViewModel
import com.example.movieapp.core.utils.network.ConnectivityObserver
import com.example.movieapp.core.utils.network.NetworkConnectivityObserver
import com.example.movieapp.domain.usecase.GetGenresUseCase
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        MovieInstance.api
    }

    single<MoviesRepository> {
        MovieRepositoryImpl(get())
    }

    single {
        GetMoviesUseCase(get())
    }

    single {
        GetGenresUseCase(get())
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
        MovieDetailsViewModel(get(), get())
    }

}