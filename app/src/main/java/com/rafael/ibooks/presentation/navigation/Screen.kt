package com.rafael.ibooks.presentation.navigation


sealed class Screen(val route: String) {
    data object BookList : Screen("book_list")
    data object BookDetail : Screen("book_detail")
}
