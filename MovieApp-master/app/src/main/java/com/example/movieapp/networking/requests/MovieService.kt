package com.example.movieapp.networking.requests

import com.example.movieapp.networking.model.Movies
import retrofit2.Response
import retrofit2.http.GET

interface MovieService {

    @GET("discover/movie")
    suspend fun getMovies(): Response<Movies>

}