package com.rafael.ibooks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rafael.ibooks.presentation.screens.BookDetailScreen
import com.rafael.ibooks.presentation.screens.BookListScreen

@Composable
fun IBooksNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.BookList.route
    ) {
        composable(Screen.BookList.route) {
            BookListScreen(
                onBookClick = { book ->
                    navController.navigate("${Screen.BookDetail.route}/${book.id}")
                }
            )
        }
        composable(
            route = "${Screen.BookDetail.route}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
            BookDetailScreen(
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
