package com.example.movieapp.koin

import com.example.movieapp.networking.instance.MovieInstance
import com.example.movieapp.viewModel.MovieDetailsViewModel
import com.example.movieapp.viewModel.MoviesViewModel
import com.example.movieapp.viewModel.SearchMoviesViewModel
import com.example.movieapp.utils.network.ConnectivityObserver
import com.example.movieapp.utils.network.NetworkConnectivityObserver
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

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidContext())
    }

    viewModel {
        MoviesViewModel(get(), get())
    }

    viewModel {
        SearchMoviesViewModel(get(), get())
    }

    viewModel {
        MovieDetailsViewModel(get(), get())
    }

}