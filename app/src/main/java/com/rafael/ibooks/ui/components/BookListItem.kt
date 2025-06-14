package com.rafael.ibooks.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .sizeIn(minHeight = 72.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(94.dp)
                    .width(64.dp)
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
                    }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(16.dp))

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
            book = BooksRepository.books.first()
        )
    }
}