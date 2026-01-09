package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_cache_table")
data class MovieCacheEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String,
    val genreIds: List<Int>,
    val page: Int
)