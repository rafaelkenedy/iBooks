package com.rafael.ibooks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import com.rafael.ibooks.presentation.state.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getRecentBooksUseCase: GetRecentBooksUseCase,
) : ViewModel() {

    private val _bookListViewState = MutableStateFlow<ViewState<List<Book>>>(ViewState.Neutral)
    val bookListViewState: StateFlow<ViewState<List<Book>>> = _bookListViewState.asStateFlow()

    private var currentQuery: String? = null

    init {
        searchBooks(query = null)
    }

    fun searchBooks(query: String?) {
        currentQuery = query
        _bookListViewState.value = ViewState.Loading
        viewModelScope.launch {
            try {
                val books = fetchBooks(page = 0)
                _bookListViewState.value = ViewState.Success(
                    data = books,
                    endReached = books.size < PAGE_SIZE
                )
            } catch (e: Exception) {
                _bookListViewState.value = ViewState.Error(e)
            }
        }
    }

    fun loadNextPage() {
        val currentState = _bookListViewState.value

        if (currentState !is ViewState.Success || currentState.isLoadingMore || currentState.endReached) {
            return
        }

        viewModelScope.launch {
            _bookListViewState.value = currentState.copy(isLoadingMore = true)
            try {
                val currentPage = currentState.data.size / PAGE_SIZE
                val newBooks = fetchBooks(page = currentPage)

                _bookListViewState.value = currentState.copy(
                    data = currentState.data + newBooks,
                    isLoadingMore = false,
                    endReached = newBooks.size < PAGE_SIZE
                )
            } catch (e: Exception) {
                _bookListViewState.value = currentState.copy(isLoadingMore = false)
            }
        }
    }

    private suspend fun fetchBooks(page: Int): List<Book> {
        val startIndex = page * PAGE_SIZE
        return if (currentQuery != null) {
            searchBooksUseCase(
                query = currentQuery!!,
                startIndex = startIndex,
                maxResults = PAGE_SIZE
            )
        } else {
            getRecentBooksUseCase(startIndex = startIndex, maxResults = PAGE_SIZE)
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}