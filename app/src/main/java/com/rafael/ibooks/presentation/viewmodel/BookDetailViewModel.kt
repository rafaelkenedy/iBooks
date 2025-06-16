package com.rafael.ibooks.presentation.viewmodel

import com.rafael.ibooks.commons.base.BaseViewModel
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.presentation.state.DetailDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BookDetailScreenState(
    val detailDataState: DetailDataState<Book>? = null
)

class BookDetailViewModel(
    private val getBookDetailsUseCase: GetBookDetailsUseCase
) : BaseViewModel() {

    private val _screenState = MutableStateFlow(BookDetailScreenState())
    val screenState: StateFlow<BookDetailScreenState> = _screenState.asStateFlow()

    fun loadBookDetails(bookId: String) {
        launch(
            retryAction = { loadBookDetails(bookId) },
            onError = { error ->
                _screenState.update { it.copy(detailDataState = DetailDataState.Error(error)) }
                sendActionableErrorEvent(error, retryAction = { loadBookDetails(bookId) })
            }
        ) {
            val book = getBookDetailsUseCase(bookId)
            _screenState.update { it.copy(detailDataState = DetailDataState.Success(book)) }
        }
    }
}