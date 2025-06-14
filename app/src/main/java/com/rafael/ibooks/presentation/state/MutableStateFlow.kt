package com.rafael.ibooks.presentation.state

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<ViewState<T>>.emitSuccess(data: T) {
    value = ViewState.Success(data)
}

fun <T> MutableStateFlow<ViewState<T>>.emitError(error: Throwable) {
    value = ViewState.Error(error)
}

fun <T> MutableStateFlow<ViewState<T>>.emitLoading() {
    value = ViewState.Loading
}

fun <T> MutableStateFlow<ViewState<T>>.emitNeutral() {
    value = ViewState.Neutral
}