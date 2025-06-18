package com.rafael.ibooks.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.ui.theme.IBooksTheme

val bookGenres = listOf(
    "Ficção", "Fantasia", "Suspense", "Romance", "Aventura",
    "Biografia", "História", "Ciência", "Tecnologia", "Negócios"
)

@Composable
fun GenreChips(
    genres: List<String>,
    selectedGenre: String?,
    onGenreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres) { genre ->
            FilterChip(
                selected = (genre == selectedGenre),
                onClick = { onGenreSelected(genre) },
                label = { Text(genre) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GenreChipsPreview() {
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    IBooksTheme {
        Surface {
            GenreChips(
                genres = bookGenres,
                selectedGenre = selectedGenre,
                onGenreSelected = { selectedGenre = it },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}