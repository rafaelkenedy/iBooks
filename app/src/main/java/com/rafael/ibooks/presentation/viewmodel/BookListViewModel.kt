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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getRecentBooksUseCase: GetRecentBooksUseCase,
    private val genreMap: Map<String, String>
) : ViewModel() {

    private val _screenState = MutableStateFlow(BookListScreenState())
    val screenState: StateFlow<BookListScreenState> = _screenState.asStateFlow()

    init {
        searchBooks(query = null)
    }

    fun onErrorDialogDismissed() {
        _screenState.update { it.copy(errorToShow = null) }
    }

    fun onQueryChange(newQuery: String) {
        _screenState.update { it.copy(searchText = newQuery) }
    }

    fun onSearch() {
        _screenState.update { it.copy(selectedGenre = null) }
        val query = _screenState.value.searchText.ifBlank { null }
        searchBooks(query)
    }

    fun onGenreSelected(genre: String) {
        val currentGenre = _screenState.value.selectedGenre
        val newGenre = if (currentGenre == genre) null else genre

        _screenState.update { it.copy(selectedGenre = newGenre, searchText = "") }

        val query = if (newGenre != null) {
            val apiTerm = genreMap[newGenre] ?: newGenre
            "subject:\"$apiTerm\""
        } else {
            null
        }

        searchBooks(query)
    }

    private fun searchBooks(query: String?) {
        viewModelScope.launch {
            _screenState.update { it.copy(booksState = ViewState.Loading) }
            try {
                val books = fetchBooks(query = query, page = 0)
                _screenState.update {
                    it.copy(
                        booksState = ViewState.Success(
                            data = books, endReached = books.size < PAGE_SIZE
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.d("ViewModelError", "Erro em searchBooks: ${e.message}")
                _screenState.update {
                    it.copy(
                        booksState = ViewState.Error(e),
                        errorToShow = e
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        val currentBooksState = _screenState.value.booksState

        if (currentBooksState !is ViewState.Success || currentBooksState.isLoadingMore || currentBooksState.endReached) {
            return
        }

        viewModelScope.launch {
            _screenState.update {
                it.copy(booksState = currentBooksState.copy(isLoadingMore = true))
            }

            try {
                val currentGenre = _screenState.value.selectedGenre
                val currentSearchText = _screenState.value.searchText
                val queryForNextPage = when {
                    currentGenre != null -> {
                        val apiTerm = genreMap[currentGenre] ?: currentGenre
                        "subject:\"$apiTerm\""
                    }

                    currentSearchText.isNotBlank() -> currentSearchText
                    else -> null
                }

                val currentPage = currentBooksState.data.size / PAGE_SIZE
                val newBooks = fetchBooks(query = queryForNextPage, page = currentPage)

                _screenState.update {
                    it.copy(
                        booksState = currentBooksState.copy(
                            data = currentBooksState.data + newBooks,
                            isLoadingMore = false,
                            endReached = newBooks.size < PAGE_SIZE
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.d("ViewModelError", "Erro em searchBooks: ${e.message}")
                _screenState.update {
                    it.copy(
                        booksState = currentBooksState.copy(isLoadingMore = false),
                        errorToShow = e
                    )
                }
            }
        }
    }

    private suspend fun fetchBooks(query: String?, page: Int): List<Book> {
        val startIndex = page * PAGE_SIZE
        return if (query != null) {
            searchBooksUseCase(
                query = query, startIndex = startIndex, maxResults = PAGE_SIZE
            )
        } else {
            getRecentBooksUseCase(startIndex = startIndex, maxResults = PAGE_SIZE)
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}


data class BookListScreenState(
    val booksState: ViewState<List<Book>> = ViewState.Neutral,
    val selectedGenre: String? = null,
    val searchText: String = "",
    val errorToShow: Throwable? = null
)