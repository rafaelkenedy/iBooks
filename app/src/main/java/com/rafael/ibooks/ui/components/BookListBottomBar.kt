package com.rafael.ibooks.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rafael.ibooks.R
import com.rafael.ibooks.presentation.screens.BookListTab

@Composable
fun BookListBottomBar(
    selectedTab: BookListTab,
    onTabSelected: (BookListTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BookListTab.Discover,
            onClick = { onTabSelected(BookListTab.Discover) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.discover)
                )
            },
            label = { Text(stringResource(R.string.discover)) }
        )
        NavigationBarItem(
            selected = selectedTab == BookListTab.WantToRead,
            onClick = { onTabSelected(BookListTab.WantToRead) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.want_to_read)
                )
            },
            label = { Text(stringResource(R.string.want_to_read)) }
        )
    }
}
