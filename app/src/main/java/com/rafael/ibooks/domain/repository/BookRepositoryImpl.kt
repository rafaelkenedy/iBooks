package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.data.remote.mappers.toDomain
import com.rafael.ibooks.data.remote.mappers.toDomainList
import com.rafael.ibooks.domain.Book
import retrofit2.HttpException
import java.io.IOException

class BookRepositoryImpl(
    private val remoteDataSource: IBookRemoteDataSource
) : IBookRepository {

    override suspend fun getBooks(
        query: String,
        orderBy: String?,
        maxResults: Int,
        startIndex: Int
    ): List<Book> {
        return try {
            remoteDataSource
                .getBooks(query, orderBy, maxResults, startIndex)
                .items
                .toDomainList()
        } catch (e: HttpException) {
            emptyList()
        } catch (e: IOException) {
            emptyList()
        }
    }

    override suspend fun getBookDetails(id: String): Book {
        val bookItemDto = remoteDataSource.getBookDetails(id)
        return bookItemDto.toDomain()
    }
}