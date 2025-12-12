package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO remove all these params except id, title, posterPath, title and overview
@Entity(tableName = "movies_table")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val adult: Boolean,
    val backdropPath: String,
    val genreIds: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int
)