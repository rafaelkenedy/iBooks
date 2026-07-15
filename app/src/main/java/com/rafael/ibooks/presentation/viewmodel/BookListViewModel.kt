package com.rafael.ibooks.presentation.viewmodel

import com.rafael.ibooks.commons.base.BaseViewModel
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GenerateBookTitlesUseCase
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.ObserveWantToReadBooksUseCase
import com.rafael.ibooks.domain.usecase.RemoveWantToReadBookUseCase
import com.rafael.ibooks.domain.usecase.SaveWantToReadBookUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import com.rafael.ibooks.presentation.state.DataState
import com.rafael.ibooks.ui.components.BookSwipeAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BookListViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getRecentBooksUseCase: GetRecentBooksUseCase,
    private val genreMap: Map<String, String>,
    private val getSuggestions: GenerateBookTitlesUseCase,
    private val observeWantToReadBooksUseCase: ObserveWantToReadBooksUseCase,
    private val saveWantToReadBook: SaveWantToReadBookUseCase,
    private val removeWantToReadBook: RemoveWantToReadBookUseCase
) : BaseViewModel() {

    private val _screenState = MutableStateFlow(BookListScreenState())
    val screenState: StateFlow<BookListScreenState> = _screenState.asStateFlow()
    private var wantToReadBookIds: Set<String> = emptySet()
    private var nextPage = 0
    private var initialBooksRequested = false

    init {
        observeSavedBooks()
        loadSuggestions()
    }

    private fun observeSavedBooks() {
        launch(loadingEvent = null) {
            observeWantToReadBooksUseCase().collect { books ->
                wantToReadBookIds = books.mapTo(mutableSetOf()) { it.id }
                _screenState.update { state ->
                    val dataState = state.dataState
                    state.copy(
                        wantToReadBooks = books,
                        dataState = if (dataState is DataState.Success) {
                            dataState.copy(data = excludeWantToReadBooks(dataState.data))
                        } else {
                            dataState
                        }
                    )
                }

                if (!initialBooksRequested) {
                    initialBooksRequested = true
                    searchBooks(query = null)
                }
            }
        }
    }

    private fun loadSuggestions() {
        _screenState.update { it.copy(suggestionsLoading = true) }

        launch(
            retryAction = { loadSuggestions() },
            onError = { error ->
                _screenState.update {
                    it.copy(suggestionsLoading = false)
                }
                sendActionableErrorEvent(error) { loadSuggestions() }
            }
        ) {
            val titles: List<String> = getSuggestions()
            _screenState.update {
                it.copy(
                    suggestions = titles,
                    suggestionsLoading = false
                )
            }
        }
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
        nextPage = 0
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
            val fetchedBooks = fetchBooks(query = query, page = nextPage)
            nextPage++
            _screenState.update {
                it.copy(
                    dataState = DataState.Success(
                        data = excludeWantToReadBooks(fetchedBooks),
                        endReached = fetchedBooks.size < PAGE_SIZE
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
            val fetchedBooks = fetchBooks(query = queryForNextPage, page = nextPage)
            nextPage++
            val newBooks = excludeWantToReadBooks(fetchedBooks)

            _screenState.update { state ->
                val latestDataState = state.dataState
                if (latestDataState is DataState.Success) {
                    state.copy(
                        dataState = latestDataState.copy(
                            data = excludeWantToReadBooks(
                                latestDataState.data + newBooks
                            ).distinctBy { book -> book.id },
                            endReached = fetchedBooks.size < PAGE_SIZE
                        )
                    )
                } else {
                    state
                }
            }
        }
    }

    fun onBookDismissed(book: Book) {
        removeBookFromCurrentResults(book)
    }

    fun onDiscoverBookSwipe(book: Book, action: BookSwipeAction) {
        when (action) {
            BookSwipeAction.SaveToWantToRead -> onBookSavedToWantToRead(book)
            BookSwipeAction.Dismiss -> onBookDismissed(book)
        }
    }

    fun onBookSavedToWantToRead(book: Book) {
        launch(loadingEvent = null) {
            saveWantToReadBook(book)
        }
        removeBookFromCurrentResults(book)
    }

    fun onWantToReadBookRemoved(book: Book) {
        launch(loadingEvent = null) {
            removeWantToReadBook(book)
        }
    }

    private fun removeBookFromCurrentResults(book: Book) {
        _screenState.update { state ->
            val currentDataState = state.dataState
            if (currentDataState is DataState.Success) {
                state.copy(
                    dataState = currentDataState.copy(
                        data = currentDataState.data.filterNot { it.id == book.id }
                    )
                )
            } else {
                state
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

    private fun excludeWantToReadBooks(books: List<Book>): List<Book> {
        return books.filterNot { it.id in wantToReadBookIds }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}

data class BookListScreenState(
    val dataState: DataState<List<Book>>? = null,
    val selectedGenre: String? = null,
    val searchText: String = "",
    val suggestions: List<String> = emptyList(),
    val suggestionsLoading: Boolean = false,
    val wantToReadBooks: List<Book> = emptyList()
)
