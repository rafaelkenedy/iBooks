package com.rafael.ibooks.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.rafael.ibooks.domain.Book

const val LIGHT_THEME = "Light Theme"
const val DARK_THEME = "Dark Theme"
const val COVER = "cover"
const val BOOK_LIST = "Book List"

val DEFAULT_SEARCH_SUGGESTIONS = listOf(
    "Harry Potter",
    "Senhor dos Anéis",
    "Sherlock Holmes",
    "O Hobbit",
    "As Crônicas de Nárnia",
    "Dom Quixote",
    "1984",
    "Orgulho e Preconceito",
    "O Código Da Vinci",
    "Cem Anos de Solidão",
    "Harry Potter",
    "Senhor dos Anéis",
    "Sherlock Holmes",
    "O Hobbit",
    "As Crônicas de Nárnia",
    "Dom Quixote",
    "1984",
    "Orgulho e Preconceito",
    "O Código Da Vinci",
)

val GENRE_MAP = mapOf(
    "Ficção" to "Fiction",
    "Fantasia" to "Fantasy",
    "Suspense" to "Thriller",
    "Romance" to "Romance",
    "Aventura" to "Adventure",
    "Biografia" to "Biography & Autobiography",
    "História" to "History",
    "Ciência" to "Science",
    "Tecnologia" to "Computers",
    "Negócios" to "Business & Economics"
)

val ORDER_BY_NEWEST = "newest"
val ORDER_BY_RELEVANCE = "relevance"

val VERTICAL_FADE_BRUSH = Brush.verticalGradient(
    0f to Color.Transparent,
    0.04f to Color.Red,
    0.96f to Color.Red,
    1f to Color.Transparent
)

val VERTICAL_FADE_BRUSH_SCREEN = Brush.verticalGradient(
    0f to Color.Transparent,
    0.05f to Color.Red,
    0.9f to Color.Red,
    1f to Color.Transparent
)

val SAMPLE_BOOK = Book(
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
