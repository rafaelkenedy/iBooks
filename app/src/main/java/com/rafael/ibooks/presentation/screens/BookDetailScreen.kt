package com.rafael.ibooks.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rafael.ibooks.presentation.state.ViewState
import com.rafael.ibooks.presentation.viewmodel.BookDetailViewModel
import com.rafael.ibooks.ui.components.BookDetailContent
import com.rafael.ibooks.ui.components.BookDetailTopAppBar
import com.rafael.ibooks.ui.components.LoadingIndicator
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookDetailScreen(
    bookId: String,
    onBackClick: () -> Unit,
    viewModel: BookDetailViewModel = koinViewModel()
) {
    val viewState by viewModel.bookDetailViewState.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBookDetails(bookId)
    }

    Scaffold(
        topBar = {
            BookDetailTopAppBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = viewState) {
                ViewState.Neutral,
                is ViewState.Loading -> {
                    LoadingIndicator()
                }

                is ViewState.Success -> {
                    val book = state.data
                    BookDetailContent(book = book)
                }

                is ViewState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Erro: ${state.throwable.message.orEmpty()}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

