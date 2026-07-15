package com.rafael.ibooks.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.R
import com.rafael.ibooks.commons.events.ErrorEvent
import com.rafael.ibooks.commons.events.LoadingEvent
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.presentation.state.DataState
import com.rafael.ibooks.presentation.viewmodel.BookListViewModel
import com.rafael.ibooks.ui.components.BookListBottomBar
import com.rafael.ibooks.ui.components.BookTopAppBar
import com.rafael.ibooks.ui.components.ErrorAlertDialog
import com.rafael.ibooks.ui.theme.IBooksTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

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
    var selectedTab by rememberSaveable { mutableStateOf(BookListTab.Discover) }

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

    LaunchedEffect(isAtBottom, selectedTab) {
        if (selectedTab == BookListTab.Discover && isAtBottom && !isLoading) {
            viewModel.loadNextPage()
        }
    }

    LaunchedEffect(screenState.dataState, isLoading, selectedTab) {
        val dataState = screenState.dataState
        if (
            selectedTab == BookListTab.Discover &&
            !isLoading &&
            dataState is DataState.Success &&
            dataState.data.isEmpty() &&
            !dataState.endReached
        ) {
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
        bottomBar = {
            BookListBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTopButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
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
                when (selectedTab) {
                    BookListTab.Discover -> DiscoverBooksContent(
                        screenState = screenState,
                        isLoading = isLoading,
                        listState = listState,
                        listTitle = listTitle,
                        onListTitleChange = { listTitle = it },
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = viewModel::onSearch,
                        onGenreSelected = viewModel::onGenreSelected,
                        onBookClick = onBookClick,
                        onBookSwipe = viewModel::onDiscoverBookSwipe
                    )

                    BookListTab.WantToRead -> WantToReadContent(
                        books = screenState.wantToReadBooks,
                        listState = listState,
                        onBookClick = onBookClick,
                        onRemoveBook = viewModel::onWantToReadBookRemoved,
                        modifier = Modifier.weight(1f)
                    )
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
