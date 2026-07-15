package com.rafael.ibooks.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.R
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.presentation.state.DataState
import com.rafael.ibooks.presentation.viewmodel.BookListScreenState
import com.rafael.ibooks.ui.components.BookList
import com.rafael.ibooks.ui.components.BookSearchBar
import com.rafael.ibooks.ui.components.BookSwipeAction
import com.rafael.ibooks.ui.components.DotLoadingIndicator
import com.rafael.ibooks.ui.components.GenreChips
import com.rafael.ibooks.ui.components.LoadingIndicator
import com.rafael.ibooks.utils.GENRE_MAP
import com.rafael.ibooks.utils.VERTICAL_FADE_BRUSH_SCREEN

@Composable
fun DiscoverBooksContent(
    screenState: BookListScreenState,
    isLoading: Boolean,
    listState: LazyListState,
    listTitle: String,
    onListTitleChange: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onGenreSelected: (String) -> Unit,
    onBookClick: (Book) -> Unit,
    onBookSwipe: (Book, BookSwipeAction) -> Unit
) {
    val initialTitle = stringResource(id = R.string.relevant_books_initial)
    val foundBooksTitle = stringResource(id = R.string.found_books)

    BookSearchBar(
        query = screenState.searchText,
        onQueryChange = { newQuery ->
            onQueryChange(newQuery)
            if (newQuery.isEmpty() && screenState.selectedGenre == null) {
                onListTitleChange(initialTitle)
            }
        },
        onSearch = {
            onSearch()
            onListTitleChange(foundBooksTitle)
        },
        searchResults = screenState.suggestions,
        isLoading = screenState.suggestionsLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )

    GenreChips(
        genres = GENRE_MAP.keys.toList(),
        selectedGenre = screenState.selectedGenre,
        onGenreSelected = { genre ->
            onGenreSelected(genre)
            onListTitleChange(
                if (screenState.selectedGenre == genre) {
                    initialTitle
                } else {
                    genre
                }
            )
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = listTitle,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    when {
        isLoading && (screenState.dataState as? DataState.Success)?.data.isNullOrEmpty() -> {
            LoadingIndicator()
        }

        screenState.dataState is DataState.Success -> {
            val books = screenState.dataState.data

            if (books.isNotEmpty()) {
                BookList(
                    books = books,
                    onItemClick = onBookClick,
                    listState = listState,
                    onSwipeAction = onBookSwipe,
                    bottomContent = if (isLoading) {
                        { DotLoadingIndicator() }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .fadingEdge(VERTICAL_FADE_BRUSH_SCREEN)
                )
            } else if (!isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.empty_search_results))
                }
            }
        }

        screenState.dataState is DataState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
