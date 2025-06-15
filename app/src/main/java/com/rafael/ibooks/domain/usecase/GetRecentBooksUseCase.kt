package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IBookRepository

class GetRecentBooksUseCase(
    private val repository: IBookRepository
) {
    suspend operator fun invoke(startIndex: Int, maxResults: Int): List<Book> {
        return repository.getBooks(
            query = "*",
            orderBy = "newest",
            startIndex = startIndex,
            maxResults = maxResults
        )
    }
}