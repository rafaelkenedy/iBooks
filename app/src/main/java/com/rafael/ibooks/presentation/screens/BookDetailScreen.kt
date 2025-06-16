package com.rafael.ibooks.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rafael.ibooks.R
import com.rafael.ibooks.commons.events.ErrorEvent
import com.rafael.ibooks.commons.events.LoadingEvent
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.presentation.state.DetailDataState
import com.rafael.ibooks.presentation.viewmodel.BookDetailViewModel
import com.rafael.ibooks.ui.components.BookDetailContent
import com.rafael.ibooks.ui.components.BookDetailTopAppBar
import com.rafael.ibooks.ui.components.ErrorAlertDialog
import com.rafael.ibooks.ui.components.LoadingIndicator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookDetailScreen(
    bookId: String,
    onBackClick: () -> Unit,
    viewModel: BookDetailViewModel = koinViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    val loadingEvent by viewModel.loadingFlow.collectAsState(initial = LoadingEvent.Hide)
    val isLoading = loadingEvent == LoadingEvent.Show
    var errorEventToShow by remember { mutableStateOf<ErrorEvent?>(null) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { event ->
            errorEventToShow = event
        }
    }

    LaunchedEffect(bookId) {
        viewModel.loadBookDetails(bookId)
    }

    errorEventToShow?.let { event ->
        ErrorAlertDialog(
            title = stringResource(id = R.string.error),
            message = when (event) {
                is ErrorEvent.NetworkError -> stringResource(id = R.string.network_error)
                is ErrorEvent.HttpError -> stringResource(
                    id = R.string.connection_error,
                    event.code
                )

                is ErrorEvent.UnknownError -> stringResource(id = R.string.generic_error)
            },
            onRetry = {
                event.retryAction()
                errorEventToShow = null
            },
            onDismiss = {
                event.onDismiss()
                errorEventToShow = null
            }
        )
    }

    Scaffold(
        topBar = {
            BookDetailTopAppBar(onBackClick = onBackClick)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (val state = screenState.detailDataState) {
                null, is DetailDataState.Success -> {
                    if (isLoading && state == null) {
                        LoadingIndicator()
                    }
                    (state as? DetailDataState.Success)?.data?.let { book ->
                        BookDetailContent(
                            book = book,
                            onReadClick = { selectedBook ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(
                                            R.string.book_saved_to_favorites,
                                            selectedBook.title
                                        ),
                                        withDismissAction = true
                                    )
                                }

                            },
                            onShareClick = { selectedBook ->
                                shareBook(context, selectedBook)
                            },
                        )
                    }
                }

                is DetailDataState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.error),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun shareBook(context: Context, book: Book) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Confira o livro '${book.title}' de ${book.author}! ${book.imageUrl}"
        )
        type = "text/plain"
    }
    context.startActivity(
        Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_book_with)
        )
    )
}