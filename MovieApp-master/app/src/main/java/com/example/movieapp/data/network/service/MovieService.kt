package com.example.movieapp.data.network.service

import com.example.movieapp.data.network.dto.details.DetailsDTO
import com.example.movieapp.data.network.dto.genres.GenresListDTO
import com.example.movieapp.data.network.dto.movies.MoviesDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("discover/movie")
    suspend fun getMovies(@Query("page") page: Int): MoviesDTO

    @GET("genre/movie/list")
    suspend fun getGenres(): GenresListDTO

    @GET("search/movie")
    suspend fun searchMovie(@Query("query") query: String): MoviesDTO

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): DetailsDTO

}