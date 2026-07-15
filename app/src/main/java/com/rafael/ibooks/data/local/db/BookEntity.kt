package com.rafael.ibooks.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "want_to_read_books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val pageCount: Int,
    val publisher: String,
    val publishedYear: Int,
    val imageUrl: String,
    val description: String,
    val rating: Double?,
    val ratingCount: Int?,
    val genres: List<String>,
    val savedAt: Long
)
