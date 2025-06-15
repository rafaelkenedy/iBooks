package com.rafael.ibooks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.presentation.state.ViewState
import com.rafael.ibooks.presentation.state.emitError
import com.rafael.ibooks.presentation.state.emitLoading
import com.rafael.ibooks.presentation.state.emitSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val getBookDetailsUseCase: GetBookDetailsUseCase
) : ViewModel() {

    private val _bookDetailViewState = MutableStateFlow<ViewState<Book>>(ViewState.Loading)
    val bookDetailViewState: StateFlow<ViewState<Book>> = _bookDetailViewState.asStateFlow()

    fun loadBookDetails(bookId: String) {
        viewModelScope.launch {
            _bookDetailViewState.emitLoading()
            try {
                val book = getBookDetailsUseCase(bookId)
                _bookDetailViewState.emitSuccess(book)
            } catch (e: Exception) {
                _bookDetailViewState.emitError(e)
            }
        }
    }
}