package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IWantToReadRepository

class RemoveWantToReadBookUseCase(
    private val repository: IWantToReadRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.removeBook(book)
    }
}
