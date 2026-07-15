package com.rafael.ibooks.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.rafael.ibooks.commons.base.MainCoroutineExtension
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GenerateBookTitlesUseCase
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.ObserveWantToReadBooksUseCase
import com.rafael.ibooks.domain.usecase.RemoveWantToReadBookUseCase
import com.rafael.ibooks.domain.usecase.SaveWantToReadBookUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import com.rafael.ibooks.presentation.state.DataState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineExtension::class)
@DisplayName("BookListViewModel")
internal class BookListViewModelTest {

    private val searchBooksUseCase = mockk<SearchBooksUseCase>()
    private val getRecentBooksUseCase = mockk<GetRecentBooksUseCase>()
    private val getSuggestions = mockk<GenerateBookTitlesUseCase>()
    private val observeWantToReadBooksUseCase = mockk<ObserveWantToReadBooksUseCase>()
    private val saveWantToReadBookUseCase = mockk<SaveWantToReadBookUseCase>(relaxed = true)
    private val removeWantToReadBookUseCase = mockk<RemoveWantToReadBookUseCase>(relaxed = true)
    private lateinit var savedBooks: MutableStateFlow<List<Book>>

    @BeforeEach
    fun setUp() {
        savedBooks = MutableStateFlow(emptyList())
        every { observeWantToReadBooksUseCase() } returns savedBooks
        coEvery { getSuggestions() } returns emptyList()
    }

    @Test
    @DisplayName("não exibe na descoberta livros que já estão no Quero Ler")
    fun excludesWantToReadBooksFromInitialResults() {
        val savedBook = book("saved")
        val availableBook = book("available")
        savedBooks.value = listOf(savedBook)
        coEvery {
            getRecentBooksUseCase(startIndex = 0, maxResults = 10)
        } returns listOf(savedBook, availableBook)

        val viewModel = createViewModel()

        val state = viewModel.screenState.value.dataState as DataState.Success
        assertThat(state.data).containsExactly(availableBook)
        assertThat(viewModel.screenState.value.wantToReadBooks).containsExactly(savedBook)
    }

    @Test
    @DisplayName("remove da descoberta um livro salvo posteriormente no Room")
    fun removesBookWhenRoomEmitsSavedBook() {
        val availableBook = book("available")
        coEvery {
            getRecentBooksUseCase(startIndex = 0, maxResults = 10)
        } returns listOf(availableBook)
        val viewModel = createViewModel()

        savedBooks.value = listOf(availableBook)

        val state = viewModel.screenState.value.dataState as DataState.Success
        assertThat(state.data).isEmpty()
    }

    @Test
    @DisplayName("mantém o índice da paginação mesmo após filtrar livros salvos")
    fun keepsApiPageIndexAfterFilteringSavedBooks() {
        val savedBook = book("saved")
        val firstAvailableBook = book("available-1")
        val secondAvailableBook = book("available-2")
        savedBooks.value = listOf(savedBook)
        coEvery {
            getRecentBooksUseCase(startIndex = 0, maxResults = 10)
        } returns List(8) { index -> book("available-$index") } +
            savedBook + firstAvailableBook
        coEvery {
            getRecentBooksUseCase(startIndex = 10, maxResults = 10)
        } returns listOf(secondAvailableBook)
        val viewModel = createViewModel()

        viewModel.loadNextPage()

        coVerify(exactly = 1) {
            getRecentBooksUseCase(startIndex = 10, maxResults = 10)
        }
    }

    private fun createViewModel() = BookListViewModel(
        searchBooksUseCase = searchBooksUseCase,
        getRecentBooksUseCase = getRecentBooksUseCase,
        genreMap = emptyMap(),
        getSuggestions = getSuggestions,
        observeWantToReadBooksUseCase = observeWantToReadBooksUseCase,
        saveWantToReadBook = saveWantToReadBookUseCase,
        removeWantToReadBook = removeWantToReadBookUseCase
    )

    private fun book(id: String) = Book(
        id = id,
        title = "Book $id",
        author = "Author",
        pageCount = 100,
        publisher = "Publisher",
        publishedYear = 2026,
        imageUrl = "",
        description = "Description",
        rating = null,
        ratingCount = null,
        genres = emptyList()
    )
}
