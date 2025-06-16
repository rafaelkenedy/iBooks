package com.rafael.ibooks.presentation.viewmodel

import com.rafael.ibooks.commons.base.BaseViewModel
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import com.rafael.ibooks.presentation.state.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getRecentBooksUseCase: GetRecentBooksUseCase,
    private val genreMap: Map<String, String>
) : BaseViewModel() {

    private val _screenState = MutableStateFlow(BookListScreenState())
    val screenState: StateFlow<BookListScreenState> = _screenState.asStateFlow()

    init {
        searchBooks(query = null)
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
        _screenState.update { it.copy(dataState = null) }

        launch(
            retryAction = { searchBooks(query) },
            onError = { error ->
                _screenState.update {
                    it.copy(dataState = DataState.Error(error))
                }
                sendActionableErrorEvent(error, retryAction = { searchBooks(query) })
            }
        ) {
            val books = fetchBooks(query = query, page = 0)
            _screenState.update {
                it.copy(
                    dataState = DataState.Success(
                        data = books, endReached = books.size < PAGE_SIZE
                    )
                )
            }
        }
    }

    fun loadNextPage() {
        val currentDataState = _screenState.value.dataState

        if (currentDataState !is DataState.Success || currentDataState.endReached) {
            return
        }

        launch(retryAction = { loadNextPage() }) {
            val queryForNextPage = getQueryForNextPage()
            val currentPage = currentDataState.data.size / PAGE_SIZE
            val newBooks = fetchBooks(query = queryForNextPage, page = currentPage)

            _screenState.update {
                it.copy(
                    dataState = currentDataState.copy(
                        data = currentDataState.data + newBooks,
                        endReached = newBooks.size < PAGE_SIZE
                    )
                )
            }
        }
    }

    private fun getQueryForNextPage(): String? {
        val state = _screenState.value
        return when {
            state.selectedGenre != null -> {
                val apiTerm = genreMap[state.selectedGenre] ?: state.selectedGenre
                "subject:\"$apiTerm\""
            }

            state.searchText.isNotBlank() -> state.searchText
            else -> null
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
    val dataState: DataState<List<Book>>? = null,
    val selectedGenre: String? = null,
    val searchText: String = ""
)