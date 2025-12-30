package com.example.movieapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class Details(val movieId: String)

@Serializable
object Search

@Serializable
object Favorites