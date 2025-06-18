package com.rafael.ibooks.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.R
import com.rafael.ibooks.commons.events.ErrorEvent
import com.rafael.ibooks.commons.events.LoadingEvent
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.presentation.state.DataState
import com.rafael.ibooks.presentation.viewmodel.BookListViewModel
import com.rafael.ibooks.ui.components.BookList
import com.rafael.ibooks.ui.components.BookSearchBar
import com.rafael.ibooks.ui.components.BookTopAppBar
import com.rafael.ibooks.ui.components.DotLoadingIndicator
import com.rafael.ibooks.ui.components.ErrorAlertDialog
import com.rafael.ibooks.ui.components.GenreChips
import com.rafael.ibooks.ui.components.LoadingIndicator
import com.rafael.ibooks.ui.theme.IBooksTheme
import com.rafael.ibooks.utils.DEFAULT_SEARCH_SUGGESTIONS
import com.rafael.ibooks.utils.GENRE_MAP
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookListScreen(
    viewModel: BookListViewModel = koinViewModel(),
    onBookClick: (book: Book) -> Unit
) {
    val screenState by viewModel.screenState.collectAsState()
    val loadingEvent by viewModel.loadingFlow.collectAsState(initial = LoadingEvent.Hide)
    val isLoading = loadingEvent == LoadingEvent.Show
    var errorEventToShow by remember { mutableStateOf<ErrorEvent?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val initialTitle = stringResource(id = R.string.relevant_books_initial)
    val foundBooksTitle = stringResource(id = R.string.found_books)

    var listTitle by remember { mutableStateOf(initialTitle) }

    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.lastOrNull()
                val buffer = 3
                lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { event ->
            errorEventToShow = event
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !isLoading) {
            viewModel.loadNextPage()
        }
    }

    errorEventToShow?.let { event ->
        val title = stringResource(R.string.error)
        val message = when (event) {
            is ErrorEvent.NetworkError -> stringResource(R.string.network_error)
            is ErrorEvent.HttpError -> stringResource(R.string.connection_error, event.code)
            is ErrorEvent.UnknownError -> stringResource(R.string.generic_error)
        }

        ErrorAlertDialog(
            title = title,
            message = message,
            onRetry = {
                event.retryAction()
                errorEventToShow = null
            },
            onDismiss = { errorEventToShow = null }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { BookTopAppBar() },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTopButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.up)
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                BookSearchBar(
                    query = screenState.searchText,
                    onQueryChange = { newQuery ->
                        viewModel.onQueryChange(newQuery)
                        if (newQuery.isEmpty() && screenState.selectedGenre == null) {
                            listTitle = initialTitle
                        }
                    },
                    onSearch = {
                        viewModel.onSearch()
                        listTitle = foundBooksTitle
                    },
                    searchResults = screenState.suggestions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                GenreChips(
                    genres = GENRE_MAP.keys.toList(),
                    selectedGenre = screenState.selectedGenre,
                    onGenreSelected = { genre ->
                        viewModel.onGenreSelected(genre)
                        listTitle = if (screenState.selectedGenre == genre) {
                            initialTitle
                        } else {
                            genre
                        }
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
                        val books = (screenState.dataState as DataState.Success<List<Book>>).data

                        if (books.isNotEmpty()) {
                            BookList(
                                books = books,
                                onItemClick = onBookClick,
                                listState = listState,
                                bottomContent = if (isLoading) {
                                    { DotLoadingIndicator() }
                                } else {
                                    null
                                }
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
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BookAppPreview() {
    IBooksTheme {
        BookListScreen(onBookClick = {})
    }
}