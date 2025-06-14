package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.data.remote.mappers.toDomainList
import com.rafael.ibooks.domain.Book

class BookRepositoryImpl(
    private val remoteDataSource: IBookRemoteDataSource
) : IBookRepository {
    override suspend fun searchBooks(query: String, maxResults: Int, startIndex: Int): List<Book> {
        return remoteDataSource
            .searchBooks(query, maxResults, startIndex)
            .items
            .toDomainList()
    }

    override suspend fun getRecentBooks(): List<Book> {
        return remoteDataSource.getRecentBooks().items.toDomainList()
    }


}