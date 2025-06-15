package com.rafael.ibooks.ui.components

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.rafael.ibooks.domain.Book
import com.rafael.ibooks.utils.spannedToAnnotatedString
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun BookDetailContent(book: Book) {
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
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Erro ao carregar imagem"
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Romance • Aventura",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(book.rating.toInt()) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                }
                repeat(5 - book.rating.toInt()) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
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

                onClick = { /* TODO: want to read */ }
            ) {
                Text("Want to Read")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: share */ }
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Book Description",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = spannedToAnnotatedString(
                HtmlCompat.fromHtml(
                    book.description.ifBlank { "No description available." },
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
