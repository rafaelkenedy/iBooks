package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.data.remote.mappers.toDomain
import com.rafael.ibooks.data.remote.mappers.toDomainList
import com.rafael.ibooks.domain.Book

class BookRepositoryImpl(
    private val remoteDataSource: IBookRemoteDataSource
) : IBookRepository {

    override suspend fun getBooks(
        query: String,
        orderBy: String?,
        maxResults: Int,
        startIndex: Int
    ): List<Book> {
        val response = remoteDataSource.getBooks(query, orderBy, maxResults, startIndex)
        return response.items.toDomainList()

    }

    override suspend fun getBookDetails(id: String): Book {
        val bookItemDto = remoteDataSource.getBookDetails(id)
        return bookItemDto.toDomain()
    }
}