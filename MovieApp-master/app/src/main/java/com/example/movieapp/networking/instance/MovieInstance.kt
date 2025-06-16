package com.example.movieapp.networking.instance

import com.example.movieapp.networking.requests.MovieService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.movieapp.BuildConfig

object MovieInstance {

    private const val BASE_URL = BuildConfig.BASE_URL
    private const val API_KEY = BuildConfig.API_KEY
    const val BASE_URL_IMAGE = "https://image.tmdb.org/t/p/w500/"

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