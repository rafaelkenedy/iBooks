package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IBookRepository

class GetBookDetailsUseCase(
    private val repository: IBookRepository
) {
    suspend operator fun invoke(id: String): Book {
        return repository.getBookDetails(id)
    }
}