package com.example.movieapp.networking.requests

import com.example.movieapp.networking.model.genres.GenresList
import com.example.movieapp.networking.model.movies.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("discover/movie")
    suspend fun getMovies(@Query("page") page: Int): Response<Movies>

    @GET("genre/movie/list")
    suspend fun getGenres(): Response<GenresList>

    @GET("search/movie")
    suspend fun searchMovie(@Query("query") query: String): Response<Movies>

}