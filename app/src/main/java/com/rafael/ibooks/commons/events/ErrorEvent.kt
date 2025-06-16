package com.rafael.ibooks.commons.events

sealed class ErrorEvent {

    abstract val retryAction: () -> Unit
    abstract val onDismiss: () -> Unit

    data class NetworkError(
        override val retryAction: () -> Unit,
        override val onDismiss: () -> Unit
    ) : ErrorEvent()

    data class HttpError(
        val code: Int,
        val message: String? = null,
        override val retryAction: () -> Unit,
        override val onDismiss: () -> Unit
    ) : ErrorEvent()

    data class UnknownError(
        val throwable: Throwable,
        override val retryAction: () -> Unit,
        override val onDismiss: () -> Unit
    ) : ErrorEvent()
}