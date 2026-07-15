package com.rafael.ibooks.data.local.mappers

import com.rafael.ibooks.data.local.db.BookEntity
import com.rafael.ibooks.domain.Book

fun Book.toEntity(savedAt: Long = System.currentTimeMillis()): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        pageCount = pageCount,
        publisher = publisher,
        publishedYear = publishedYear,
        imageUrl = imageUrl,
        description = description,
        rating = rating,
        ratingCount = ratingCount,
        genres = genres,
        savedAt = savedAt
    )
}

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        pageCount = pageCount,
        publisher = publisher,
        publishedYear = publishedYear,
        imageUrl = imageUrl,
        description = description,
        rating = rating,
        ratingCount = ratingCount,
        genres = genres
    )
}
