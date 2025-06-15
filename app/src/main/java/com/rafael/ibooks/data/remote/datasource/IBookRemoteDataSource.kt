package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.BookItem
import com.rafael.ibooks.data.remote.model.BooksResponse

interface IBookRemoteDataSource {
    suspend fun getBooks(query: String, orderBy: String?, maxResults: Int, startIndex: Int): BooksResponse
    suspend fun getBookDetails(id: String): BookItem
}