package com.rafael.ibooks.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.rafael.ibooks.R
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.ui.theme.IBooksTheme
import com.rafael.ibooks.utils.DARK_THEME
import com.rafael.ibooks.utils.LIGHT_THEME
import com.rafael.ibooks.utils.spannedToAnnotatedString
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.floor

@Composable
fun BookDetailContent(
    book: Book,
    onReadClick: (Book) -> Unit,
    onShareClick: (Book) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = { book.imageUrl },
            modifier = Modifier
                .height(320.dp)
                .clip(RoundedCornerShape(12.dp)),
            imageOptions = ImageOptions(
                contentDescription = book.title,
                contentScale = ContentScale.Crop
            ),
            loading = {
                LoadingIndicator()
            },
            failure = {
                Image(
                    painter = painterResource(id = R.drawable.no_cover_available),
                    contentDescription = stringResource(R.string.erro_loading_img)
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        if (book.genres.isNotEmpty()) {
            Text(
                text = book.genres.joinToString(" • "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = "by ${book.author}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        if (book.rating != null) {
            val rating = book.rating.toFloat()
            val starColor = MaterialTheme.colorScheme.secondary

            Row(verticalAlignment = Alignment.CenterVertically) {
                val fullStars = floor(rating).toInt()
                val halfStar = if (rating - fullStars >= 0.5f) 1 else 0
                val emptyStars = 5 - fullStars - halfStar

                repeat(fullStars) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = starColor)
                }
                if (halfStar == 1) {
                    Icon(
                        Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = starColor
                    )
                }
                repeat(emptyStars) {
                    Icon(Icons.Outlined.Star, contentDescription = null, tint = starColor)
                }

                Spacer(Modifier.width(8.dp))
                Text("${book.rating} (${book.ratingCount ?: 0})")
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                modifier = Modifier.weight(1f),

                onClick = { onReadClick(book) }
            ) {
                Text(stringResource(R.string.want_to_read))
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onShareClick(book) }
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.book_description),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = spannedToAnnotatedString(
                HtmlCompat.fromHtml(
                    book.description.ifBlank { stringResource(R.string.no_description_available) },
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(LIGHT_THEME)
@Preview(DARK_THEME, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BookDetailContentPreview() {
    IBooksTheme(dynamicColor = false) {
        val sampleBook = Book(
            id = "1",
            title = "O Senhor dos Anéis",
            author = "J.R.R. Tolkien",
            description = "Uma jornada épica através da Terra Média para destruir um anel maligno.",
            imageUrl = "https://example.com/lotr.jpg",
            publishedYear = 1954,
            rating = 4.8,
            ratingCount = 12000,
            pageCount = 300,
            publisher = "J.R.R. Tolkien",
            genres = listOf("Fantasia", "Aventura")
        )
        BookDetailContent(
            book = sampleBook,
            onReadClick = { book -> println("Quero ler: ${book.title}") },
            onShareClick = { book -> println("Compartilhar: ${book.title}") }
        )
    }
}
