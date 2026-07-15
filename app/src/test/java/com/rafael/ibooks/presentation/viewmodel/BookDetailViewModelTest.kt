package com.rafael.ibooks.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.rafael.ibooks.commons.base.MainCoroutineExtension
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.domain.usecase.ObserveWantToReadBooksUseCase
import com.rafael.ibooks.domain.usecase.SaveWantToReadBookUseCase
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
@DisplayName("BookDetailViewModel")
internal class BookDetailViewModelTest {

    private val getBookDetailsUseCase = mockk<GetBookDetailsUseCase>()
    private val observeWantToReadBooksUseCase = mockk<ObserveWantToReadBooksUseCase>()
    private val saveWantToReadBookUseCase = mockk<SaveWantToReadBookUseCase>(relaxed = true)
    private lateinit var savedBooks: MutableStateFlow<List<Book>>

    @BeforeEach
    fun setUp() {
        savedBooks = MutableStateFlow(emptyList())
        every { observeWantToReadBooksUseCase() } returns savedBooks
    }

    @Test
    @DisplayName("marca como Quero Ler quando o livro já está salvo no Room")
    fun marksBookAsWantToReadWhenAlreadySaved() {
        val book = book("saved")
        savedBooks.value = listOf(book)
        coEvery { getBookDetailsUseCase(book.id) } returns book
        val viewModel = createViewModel()

        viewModel.loadBookDetails(book.id)

        assertThat(viewModel.screenState.value.isWantToRead).isTrue()
    }

    @Test
    @DisplayName("atualiza o detalhe quando o Room recebe o livro aberto")
    fun updatesDetailWhenRoomEmitsCurrentBook() {
        val book = book("current")
        coEvery { getBookDetailsUseCase(book.id) } returns book
        val viewModel = createViewModel()
        viewModel.loadBookDetails(book.id)

        savedBooks.value = listOf(book)

        assertThat(viewModel.screenState.value.isWantToRead).isTrue()
    }

    @Test
    @DisplayName("salva o livro apenas uma vez ao tocar em Quero Ler")
    fun savesBookOnlyOnce() {
        val book = book("new")
        coEvery { getBookDetailsUseCase(book.id) } returns book
        val viewModel = createViewModel()
        viewModel.loadBookDetails(book.id)

        viewModel.onWantToReadClick(book)
        viewModel.onWantToReadClick(book)

        assertThat(viewModel.screenState.value.isWantToRead).isTrue()
        coVerify(exactly = 1) { saveWantToReadBookUseCase(book) }
    }

    private fun createViewModel() = BookDetailViewModel(
        getBookDetailsUseCase = getBookDetailsUseCase,
        observeWantToReadBooksUseCase = observeWantToReadBooksUseCase,
        saveWantToReadBookUseCase = saveWantToReadBookUseCase
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
