package com.rafael.ibooks.presentation.viewmodel

import com.rafael.ibooks.commons.base.BaseViewModel
import com.rafael.ibooks.commons.events.UiEvent
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.domain.usecase.ObserveWantToReadBooksUseCase
import com.rafael.ibooks.domain.usecase.SaveWantToReadBookUseCase
import com.rafael.ibooks.presentation.state.DetailDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BookDetailScreenState(
    val detailDataState: DetailDataState<Book>? = null,
    val isWantToRead: Boolean = false
)

class BookDetailViewModel(
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val observeWantToReadBooksUseCase: ObserveWantToReadBooksUseCase,
    private val saveWantToReadBookUseCase: SaveWantToReadBookUseCase
) : BaseViewModel() {

    private val _screenState = MutableStateFlow(BookDetailScreenState())
    val screenState: StateFlow<BookDetailScreenState> = _screenState.asStateFlow()
    private var wantToReadBookIds: Set<String> = emptySet()

    init {
        observeWantToReadBooks()
    }

    fun onBackClick() {
        sendUiEventOnce(UiEvent.NavigateBack)
    }

    fun loadBookDetails(bookId: String) {
        launch(
            retryAction = { loadBookDetails(bookId) },
            onError = { error ->
                _screenState.update { it.copy(detailDataState = DetailDataState.Error(error)) }
                sendActionableErrorEvent(error, retryAction = { loadBookDetails(bookId) })
            }
        ) {
            val book = getBookDetailsUseCase(bookId)
            _screenState.update {
                it.copy(
                    detailDataState = DetailDataState.Success(book),
                    isWantToRead = book.id in wantToReadBookIds
                )
            }
        }
    }

    fun onWantToReadClick(book: Book) {
        if (_screenState.value.isWantToRead) return

        _screenState.update { it.copy(isWantToRead = true) }
        launch(
            loadingEvent = null,
            onError = { error ->
                _screenState.update { it.copy(isWantToRead = false) }
                sendActionableErrorEvent(error, retryAction = { onWantToReadClick(book) })
            }
        ) {
            saveWantToReadBookUseCase(book)
        }
    }

    private fun observeWantToReadBooks() {
        launch(loadingEvent = null) {
            observeWantToReadBooksUseCase().collect { books ->
                wantToReadBookIds = books.mapTo(mutableSetOf()) { it.id }
                val currentBook =
                    (_screenState.value.detailDataState as? DetailDataState.Success)?.data
                _screenState.update {
                    it.copy(isWantToRead = currentBook?.id in wantToReadBookIds)
                }
            }
        }
    }
}
