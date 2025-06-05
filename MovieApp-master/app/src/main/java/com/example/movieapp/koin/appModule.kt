package com.example.movieapp.koin

import com.example.movieapp.networking.instance.MovieInstance
import com.example.movieapp.networking.viewModel.MoviesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        MovieInstance.api
    }

    single<MoviesRepository> {
        MovieRepositoryImpl(get())
    }

    viewModel {
        MoviesViewModel(get())
    }

}