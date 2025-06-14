package com.rafael.ibooks.data.remote.mappers

import com.rafael.ibooks.data.remote.model.BookItem
import com.rafael.ibooks.domain.Book

fun BookItem.toDomain(): Book {
    val volume = this.volumeInfo
    return Book(
        title = volume.title ?: "Título desconhecido",
        author = volume.authors?.joinToString(", ") ?: "Autor desconhecido",
        pageCount = volume.pageCount ?: 0,
        publisher = volume.publisher ?: "Editora desconhecida",
        publishedYear = volume.publishedYear ?: 0,
        imageUrl = volume.thumbnailSafe
    )
}

fun List<BookItem>?.toDomainList(): List<Book> {
    return this?.map { it.toDomain() } ?: emptyList()
}
