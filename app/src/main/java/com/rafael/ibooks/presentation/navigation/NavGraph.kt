package com.rafael.ibooks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rafael.ibooks.commons.events.UiEvent
import com.rafael.ibooks.presentation.screens.BookDetailScreen
import com.rafael.ibooks.presentation.screens.BookListScreen
import com.rafael.ibooks.presentation.viewmodel.BookDetailViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.koinViewModel

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
            val viewModel: BookDetailViewModel = koinViewModel()
            val bookId = backStackEntry.arguments?.getString("bookId")!!

            BookDetailScreen(
                bookId = bookId,
                onBackClick = { viewModel.onBackClick() },
                viewModel = viewModel
            )
            LaunchedEffect(viewModel.uiEventFlow) {
                viewModel.uiEventFlow
                    .filterIsInstance<UiEvent.NavigateBack>()
                    .first()
                navController.popBackStack()
            }
        }
    }
}
