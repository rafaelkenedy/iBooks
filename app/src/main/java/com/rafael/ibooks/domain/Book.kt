package com.rafael.ibooks.domain

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val pageCount: Int,
    val publisher: String,
    val publishedYear: Int,
    val imageUrl: String,
    val description: String,
    val rating: Double?,
    val ratingCount: Int?,
    val genres: List<String>
)