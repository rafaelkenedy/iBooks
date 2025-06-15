package com.rafael.ibooks.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun BookListItem(
    book: Book,
    onItemClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clickable { onItemClick(book) }
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
                        CircularProgressIndicator()
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

@Preview(LIGHT_THEME)
@Preview(DARK_THEME, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookListItemPreview() {

    IBooksTheme {
        BookListItem(
            book = BooksRepository.getBooks().first(),
            onItemClick = {}
        )
    }
}