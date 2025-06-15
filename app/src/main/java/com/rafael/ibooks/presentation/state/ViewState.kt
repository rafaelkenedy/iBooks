package com.rafael.ibooks.presentation.state

sealed class ViewState<out T> {
    data object Neutral : ViewState<Nothing>()
    data object Loading : ViewState<Nothing>()
    data class Success<T>(
        val data: T,
        val isLoadingMore: Boolean = false,
        val endReached: Boolean = false
    ) : ViewState<T>()
    data class Error(val throwable: Throwable) : ViewState<Nothing>()
}