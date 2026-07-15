package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.local.db.WantToReadBookDao
import com.rafael.ibooks.data.local.mappers.toDomain
import com.rafael.ibooks.data.local.mappers.toEntity
import com.rafael.ibooks.domain.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WantToReadRepositoryImpl(
    private val dao: WantToReadBookDao
) : IWantToReadRepository {

    override fun observeBooks(): Flow<List<Book>> {
        return dao.observeBooks().map { books ->
            books.map { it.toDomain() }
        }
    }

    override suspend fun saveBook(book: Book) {
        dao.saveBook(book.toEntity())
    }

    override suspend fun removeBook(book: Book) {
        dao.removeBook(book.id)
    }
}
