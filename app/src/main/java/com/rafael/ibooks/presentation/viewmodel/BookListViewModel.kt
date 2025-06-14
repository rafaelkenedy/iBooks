package com.rafael.ibooks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import com.rafael.ibooks.presentation.state.ViewState
import com.rafael.ibooks.presentation.state.emitError
import com.rafael.ibooks.presentation.state.emitLoading
import com.rafael.ibooks.presentation.state.emitSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getRecentBooksUseCase: GetRecentBooksUseCase
) : ViewModel() {

    private val _bookListViewState = MutableStateFlow<ViewState<List<Book>>>(ViewState.Neutral)
    val bookListViewState: StateFlow<ViewState<List<Book>>> = _bookListViewState

    init {
        loadRecentBooks()
    }

    fun loadRecentBooks() {
        viewModelScope.launch {
            _bookListViewState.emitLoading()
            try {
                val books = getRecentBooksUseCase()
                _bookListViewState.emitSuccess(books)
            } catch (e: Exception) {
                _bookListViewState.emitError(e)
            }
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _bookListViewState.emitLoading()
            try {
                val result = searchBooksUseCase(query)
                _bookListViewState.emitSuccess(result)
            } catch (e: Exception) {
                _bookListViewState.emitError(e)
            }
        }
    }
}