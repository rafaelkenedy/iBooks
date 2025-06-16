package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IBookRepository
import com.rafael.ibooks.utils.ORDER_BY_NEWEST

class GetRecentBooksUseCase(
    private val repository: IBookRepository
) {
    suspend operator fun invoke(startIndex: Int, maxResults: Int): List<Book> {
        return repository.getBooks(
            query = "*",
            orderBy = ORDER_BY_NEWEST,
            startIndex = startIndex,
            maxResults = maxResults
        )
    }
}