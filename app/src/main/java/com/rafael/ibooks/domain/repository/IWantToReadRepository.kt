package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.domain.Book
import kotlinx.coroutines.flow.Flow

interface IWantToReadRepository {
    fun observeBooks(): Flow<List<Book>>
    suspend fun saveBook(book: Book)
    suspend fun removeBook(book: Book)
}
