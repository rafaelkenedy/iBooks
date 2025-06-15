package com.rafael.ibooks.presentation.navigation


sealed class Screen(val route: String) {
    object BookList : Screen("book_list")
    object BookDetail : Screen("book_detail")
}
