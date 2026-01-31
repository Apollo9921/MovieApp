package com.example.movieapp.presentation.utils

sealed class TopBarAction {
    object None : TopBarAction()

    data class Details(
        val isFavorite: Boolean,
        val onClick: () -> Unit
    ) : TopBarAction()

    data class Favorite(
        val iconRes: Int,
        val onClick: () -> Unit
    ) : TopBarAction()
}