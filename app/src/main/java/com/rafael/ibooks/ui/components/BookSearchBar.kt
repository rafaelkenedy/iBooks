package com.rafael.ibooks.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.rafael.ibooks.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    searchResults: List<String>,
    isLoading: Boolean = false,

) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = modifier.fillMaxWidth(),
        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { newQuery ->
                    onQueryChange(newQuery)
                    if (newQuery.isNotEmpty()) {
                        expanded = true
                    } else {
                        expanded = false
                    }
                },
                onSearch = {
                    onSearch()
                    expanded = false
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text(stringResource(R.string.search_books)) },
                leadingIcon = {
                    Box(contentAlignment = Alignment.Center) {
                        FadeInExitInstantly(visible = expanded) {
                            IconButton(onClick = {
                                expanded = false
                                onQueryChange("")
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back_icon)
                                )
                            }
                        }

                        FadeInExitInstantly(visible = !expanded) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_icon)
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(0.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                itemsIndexed(
                    items = searchResults,
                    key = { index, result -> "$index-$result" }
                ) { index, result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable(role = Role.Button) {
                                onQueryChange(result)
                                onSearch()
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
                }

                if (searchResults.isEmpty() && query.isNotEmpty() && !isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.no_suggestions))
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun FadeInExitInstantly(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = ExitTransition.None
    ) {
        content()
    }
}