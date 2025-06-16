package com.rafael.ibooks.presentation.state

sealed class DataState<out T> {
    data class Success<T>(
        val data: T,
        val endReached: Boolean = false
    ) : DataState<T>()

    data class Error(val throwable: Throwable) : DataState<Nothing>()
}

sealed class DetailDataState<out T> {
    data class Success<T>(val data: T) : DetailDataState<T>()
    data class Error(val throwable: Throwable) : DetailDataState<Nothing>()
}