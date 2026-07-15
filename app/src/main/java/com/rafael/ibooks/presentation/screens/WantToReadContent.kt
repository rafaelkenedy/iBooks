package com.rafael.ibooks.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.rafael.ibooks.ui.components.BookList
import com.rafael.ibooks.ui.components.BookSwipeAction
import com.rafael.ibooks.utils.VERTICAL_FADE_BRUSH_SCREEN

@Composable
fun WantToReadContent(
    books: List<Book>,
    listState: LazyListState,
    onBookClick: (Book) -> Unit,
    onRemoveBook: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.want_to_read),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    if (books.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.empty_want_to_read))
        }
    } else {
        BookList(
            books = books,
            onItemClick = onBookClick,
            listState = listState,
            onSwipeAction = { book, action ->
                if (action == BookSwipeAction.Dismiss) {
                    onRemoveBook(book)
                }
            },
            enableSwipeStartToEnd = false,
            enableSwipeEndToStart = true,
            modifier = modifier.fadingEdge(VERTICAL_FADE_BRUSH_SCREEN)
        )
    }
}
