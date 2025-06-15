package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.BookItem
import com.rafael.ibooks.data.remote.model.BooksResponse
import com.rafael.ibooks.data.remote.service.IGoogleBooksApi

class BookRemoteDatasourceImpl(
    private val googleBooksApi: IGoogleBooksApi
) : IBookRemoteDataSource {

    override suspend fun getBooks(
        query: String,
        orderBy: String?,
        maxResults: Int,
        startIndex: Int
    ): BooksResponse {
        return googleBooksApi.getBooks(query, orderBy, maxResults, startIndex)
    }

    override suspend fun getBookDetails(id: String): BookItem {
        return googleBooksApi.getBookById(id)
    }
}
