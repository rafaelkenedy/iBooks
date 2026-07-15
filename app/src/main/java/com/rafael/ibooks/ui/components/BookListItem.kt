package com.rafael.ibooks.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.R
import com.rafael.ibooks.data_local.BooksRepository
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.ui.theme.IBooksTheme
import com.rafael.ibooks.utils.COVER
import com.rafael.ibooks.utils.DARK_THEME
import com.rafael.ibooks.utils.LIGHT_THEME
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListItem(
    book: Book,
    onItemClick: (Book) -> Unit,
    onSwipeAction: (Book, BookSwipeAction) -> Unit = { _, _ -> },
    enableSwipeStartToEnd: Boolean = true,
    enableSwipeEndToStart: Boolean = true,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                onSwipeAction(book, BookSwipeAction.SaveToWantToRead)
            }

            SwipeToDismissBoxValue.EndToStart -> {
                onSwipeAction(book, BookSwipeAction.Dismiss)
            }

            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = enableSwipeStartToEnd,
        enableDismissFromEndToStart = enableSwipeEndToStart,
        backgroundContent = {
            SwipeActionBackground(
                showSaveAction = enableSwipeStartToEnd,
                showDismissAction = enableSwipeEndToStart
            )
        },
        modifier = modifier
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.clickable { onItemClick(book) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .sizeIn(minHeight = 72.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(68.dp)
                        .clip(RoundedCornerShape((8.dp)))
                ) {
                    GlideImage(
                        imageModel = { book.imageUrl },
                        imageOptions = ImageOptions(
                            contentDescription = book.title.plus(COVER),
                            alignment = Alignment.TopCenter,
                            contentScale = ContentScale.Crop
                        ),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        previewPlaceholder = painterResource(R.drawable.img_place_holder),
                        failure = {
                            Image(
                                painter = painterResource(R.drawable.img_place_holder),
                                contentDescription = stringResource(R.string.image_error),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = book.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${book.publishedYear}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = book.author,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun SwipeActionBackground(
    showSaveAction: Boolean,
    showDismissAction: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (showSaveAction) {
            SwipeActionLabel(
                label = stringResource(R.string.want_to_read),
                alignment = Alignment.CenterStart,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.want_to_read),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        if (showDismissAction) {
            SwipeActionLabel(
                label = stringResource(R.string.discard_book),
                alignment = Alignment.CenterEnd,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.discard_book),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun SwipeActionLabel(
    label: String,
    alignment: Alignment,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .width(180.dp)
            .background(containerColor)
            .padding(horizontal = 24.dp),
        horizontalArrangement = if (alignment == Alignment.CenterStart) {
            Arrangement.Start
        } else {
            Arrangement.End
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (alignment == Alignment.CenterStart) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
        if (alignment == Alignment.CenterEnd) {
            Spacer(modifier = Modifier.width(8.dp))
            icon()
        }
    }
}

@Preview(LIGHT_THEME)
@Preview(DARK_THEME, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookListItemPreview() {

    IBooksTheme(
        dynamicColor = false
    ) {
        BookListItem(
            book = BooksRepository.getBooks().first(),
            onItemClick = {}
        )
    }
}
