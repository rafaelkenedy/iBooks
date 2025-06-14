package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.BooksResponse

interface IBookRemoteDataSource {
    suspend fun searchBooks(query: String, maxResults: Int = 10, startIndex: Int = 0): BooksResponse
    suspend fun getRecentBooks(): BooksResponse
}