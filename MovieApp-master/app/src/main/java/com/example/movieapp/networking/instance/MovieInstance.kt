package com.example.movieapp.networking.instance

import com.example.movieapp.networking.requests.MovieService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MovieInstance {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxNzhkNTNkMzU1OWU0NGQzNjczMjA2Yzk5NGFlOTFjNyIsIm5iZiI6MTc0NjQzMzY3My45NjIsInN1YiI6IjY4MTg3Njg5MTVjOTEwMmQyMDA4YTRmOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.szUFk_jw_YO4X4MdWysqCvIUOC1MEBmirDdsgKqO9ac"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $API_KEY")
            .header("Accept", "application/json")
            .build()
        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(headerInterceptor)
        .build()

    val api: MovieService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MovieService::class.java)
    }
}