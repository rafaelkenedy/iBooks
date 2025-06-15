package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.domain.Book

interface IBookRepository {
    suspend fun getBooks(
        query: String,
        orderBy: String?,
        maxResults: Int,
        startIndex: Int
    ): List<Book>

    suspend fun getBookDetails(id: String): Book
}