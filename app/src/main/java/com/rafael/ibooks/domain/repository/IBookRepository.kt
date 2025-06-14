package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.domain.Book

interface IBookRepository {
    suspend fun searchBooks(query: String, maxResults: Int = 10, startIndex: Int = 0): List<Book>
    suspend fun getRecentBooks(): List<Book>
}