package com.rafael.ibooks.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookConverters {
    private val gson = Gson()
    private val genresType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromGenres(genres: List<String>): String {
        return gson.toJson(genres)
    }

    @TypeConverter
    fun toGenres(value: String): List<String> {
        return gson.fromJson(value, genresType) ?: emptyList()
    }
}
