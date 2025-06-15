package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IBookRepository

class SearchBooksUseCase(
    private val repository: IBookRepository
) {
    suspend operator fun invoke(query: String, maxResults: Int, startIndex: Int): List<Book> {
        return repository.getBooks(
            query = query,
            orderBy = null,
            startIndex = startIndex,
            maxResults = maxResults
        )
    }
}