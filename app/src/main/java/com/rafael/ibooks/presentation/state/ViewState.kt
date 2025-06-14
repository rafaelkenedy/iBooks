package com.rafael.ibooks.presentation.state

sealed class ViewState<out T> {

    object Neutral : ViewState<Nothing>()
    object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(val throwable: Throwable) : ViewState<Nothing>()
}