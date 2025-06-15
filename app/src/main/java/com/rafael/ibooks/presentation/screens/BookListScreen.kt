package com.rafael.ibooks.presentation.viewmodel.screens

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.R
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.presentation.state.ViewState
import com.rafael.ibooks.presentation.viewmodel.BookListViewModel
import com.rafael.ibooks.ui.components.BookList
import com.rafael.ibooks.ui.components.BookSearchBar
import com.rafael.ibooks.ui.components.BookTopAppBar
import com.rafael.ibooks.ui.components.LoadingIndicator
import com.rafael.ibooks.ui.theme.IBooksTheme
import com.rafael.ibooks.utils.DEFAULT_SEARCH_SUGGESTIONS
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookListScreen(
    viewModel: BookListViewModel = koinViewModel(),
    onBookClick: (book: Book) -> Unit
) {
    val viewState by viewModel.bookListViewState.collectAsState()
    var searchText by rememberSaveable { mutableStateOf("") }
    val suggestions = DEFAULT_SEARCH_SUGGESTIONS

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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

    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom) {
            viewModel.loadNextPage()
        }
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
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Voltar ao topo"
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
                    query = searchText,
                    onQueryChange = { q -> searchText = q },
                    onSearch = {
                        viewModel.searchBooks(searchText.ifBlank { null })
                    },
                    searchResults = suggestions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = viewState) {
                    is ViewState.Loading -> {
                        LoadingIndicator()
                    }

                    is ViewState.Success -> {
                        if (state.data.isNotEmpty()) {
                            BookList(
                                books = state.data,
                                onItemClick = onBookClick,
                                listState = listState
                            )
                            if (state.isLoadingMore) {
                                LoadingIndicator()
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(R.string.empty_search_results))
                            }
                        }
                    }

                    is ViewState.Error -> {
                        val error = state.throwable.message
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = error.orEmpty(), color = MaterialTheme.colorScheme.error)
                        }
                    }

                    ViewState.Neutral -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.neutral_message))
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