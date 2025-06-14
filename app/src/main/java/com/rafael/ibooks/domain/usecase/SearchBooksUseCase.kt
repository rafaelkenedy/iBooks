package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IBookRepository

class SearchBooksUseCase(
    private val repository: IBookRepository
) {
    suspend operator fun invoke(query: String, maxResults: Int = 10, startIndex: Int = 0): List<Book> {
        return repository.searchBooks(query, maxResults, startIndex)
    }
}