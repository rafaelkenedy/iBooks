package com.rafael.ibooks.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring.DampingRatioNoBouncy
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.data_local.BooksRepository
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.ui.theme.IBooksTheme
import com.rafael.ibooks.utils.BOOK_LIST

@Composable
fun BookList(
    books: List<Book>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(dampingRatio = DampingRatioNoBouncy)
        ),
        exit = fadeOut(),
        modifier = modifier
    ) {
        LazyColumn(contentPadding = contentPadding) {
            itemsIndexed(books) { index, book ->
                BookListItem(
                    book = book,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .animateEnterExit(
                            enter = slideInVertically(
                                animationSpec = spring(
                                    stiffness = StiffnessLow,
                                    dampingRatio = DampingRatioNoBouncy
                                ),
                                initialOffsetY = { it * (index + 1) }
                            )
                        )
                )
            }
        }
    }
}

@Preview(BOOK_LIST)
@Composable
fun BookListPreview() {
    IBooksTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BookList(books = BooksRepository.books)
        }
    }

}




























