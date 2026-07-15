package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.repository.IWantToReadRepository
import kotlinx.coroutines.flow.Flow

class ObserveWantToReadBooksUseCase(
    private val repository: IWantToReadRepository
) {
    operator fun invoke(): Flow<List<Book>> {
        return repository.observeBooks()
    }
}
