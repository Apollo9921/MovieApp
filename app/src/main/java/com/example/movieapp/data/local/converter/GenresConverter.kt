package com.example.movieapp.data.local.converter

import androidx.room.TypeConverter

class GenresConverter {
    @TypeConverter
    fun fromGenreIdsList(genreIds: List<Int>?): String? {
        return genreIds?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toGenreIdsList(genreIdsString: String?): List<Int>? {
        return genreIdsString?.split(",")?.mapNotNull { it.toIntOrNull() }
    }
}