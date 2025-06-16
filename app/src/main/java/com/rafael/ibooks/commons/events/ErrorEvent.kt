package com.rafael.ibooks.commons.events

data class DialogAction(
    val text: String,
    val execute: () -> Unit
)

sealed class ErrorEvent {
    data object NetworkError : ErrorEvent()
    data class HttpError(val code: Int, val message: String? = null) : ErrorEvent()
    data class UnknownError(val throwable: Throwable) : ErrorEvent()
}