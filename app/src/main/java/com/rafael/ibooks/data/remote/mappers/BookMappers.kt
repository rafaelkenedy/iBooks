package com.rafael.ibooks.data.remote.mappers

import com.rafael.ibooks.data.remote.model.BookItem
import com.rafael.ibooks.data.remote.utils.ApiConstants.UNKNOWN_AUTHOR
import com.rafael.ibooks.data.remote.utils.ApiConstants.UNKNOWN_PUBLISHER
import com.rafael.ibooks.data.remote.utils.ApiConstants.UNKNOWN_TITLE
import com.rafael.ibooks.domain.Book

fun BookItem.toDomain(): Book {
    val volume = this.volumeInfo
    return Book(
        id = this.id,
        title = volume.title ?: UNKNOWN_TITLE,
        author = volume.authors?.joinToString(", ") ?: UNKNOWN_AUTHOR,
        pageCount = volume.pageCount ?: 0,
        publisher = volume.publisher ?: UNKNOWN_PUBLISHER,
        publishedYear = volume.publishedYear ?: 0,
        imageUrl = volume.thumbnailSafe,
        description = volume.description ?: "",
        rating = volume.averageRating,
        ratingCount = volume.ratingsCount,
        genres = volume.categories ?: emptyList()
    )
}

fun List<BookItem>?.toDomainList(): List<Book> {
    return this?.map { it.toDomain() } ?: emptyList()
}
