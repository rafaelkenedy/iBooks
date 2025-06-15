package com.rafael.ibooks.presentation.viewmodel.screens

//import com.rafael.ibooks.ui.components.TopAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.presentation.screens.SplashScreen
import com.rafael.ibooks.presentation.state.ViewState
import com.rafael.ibooks.presentation.viewmodel.BookListViewModel
import com.rafael.ibooks.ui.components.BookList
import com.rafael.ibooks.ui.components.BookSearchBar
import com.rafael.ibooks.ui.components.IBookTopAppBar
import com.rafael.ibooks.ui.theme.IBooksTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookListScreen(
    viewModel: BookListViewModel = koinViewModel()
) {
    val viewState by viewModel.bookListViewState.collectAsState()
    var searchText by rememberSaveable { mutableStateOf("") }
    val suggestions = listOf(
        "Harry Potter",
        "Senhor dos Anéis",
        "Sherlock Holmes",
        "O Hobbit",
        "As Crônicas de Nárnia",
        "Dom Quixote",
        "1984",
        "Orgulho e Preconceito",
        "O Código Da Vinci",
        "Cem Anos de Solidão"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        //topBar = { TopAppBar() }
        topBar = { IBookTopAppBar() },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ){
                    BookSearchBar(
                        query = searchText,
                        onQueryChange = { q -> searchText = q },
                        onSearch = {
                            if (searchText.isNotBlank()) {
                                viewModel.searchBooks(searchText)
                            }
                        },
                        searchResults = suggestions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    when (viewState) {
                        is ViewState.Loading -> {
                            IBooksTheme {
                                SplashScreen { Unit }
                            }
                        }

                        is ViewState.Success -> {
                            val books = (viewState as ViewState.Success).data
                            BookList(books = books, contentPadding = PaddingValues(0.dp))
                        }

                        is ViewState.Error -> {
                            val error = (viewState as ViewState.Error).throwable.message
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = error.orEmpty(),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        ViewState.Neutral -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                contentAlignment = Alignment.Center
                            ) {
                            }
                        }

                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BookAppPreview() {
    IBooksTheme {
        BookListScreen()
    }
}