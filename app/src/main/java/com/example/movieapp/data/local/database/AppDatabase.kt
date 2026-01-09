package com.example.movieapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieapp.data.local.converter.GenresConverter
import com.example.movieapp.data.local.dao.MovieDao
import com.example.movieapp.data.local.entity.GenreEntity
import com.example.movieapp.data.local.entity.MovieCacheEntity
import com.example.movieapp.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class, GenreEntity::class, MovieCacheEntity::class], version = 1, exportSchema = false)
@TypeConverters(GenresConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}