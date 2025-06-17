package com.rafael.ibooks.commons.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafael.ibooks.commons.events.ErrorEvent
import com.rafael.ibooks.commons.events.LoadingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    private val errorEventChannel = Channel<ErrorEvent>(Channel.BUFFERED)
    val errorFlow = errorEventChannel.receiveAsFlow()

    private val loadingChannel = Channel<LoadingEvent>(Channel.BUFFERED)
    val loadingFlow = loadingChannel.receiveAsFlow()

    open fun onErrorDismissed() {}

    fun launch(
        retryAction: (() -> Unit)? = null,
        onError: (suspend CoroutineScope.(Throwable) -> Unit)? = null,
        loadingEvent: LoadingEvent? = LoadingEvent.Show,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            if (loadingEvent == LoadingEvent.Show) {
                loadingChannel.send(LoadingEvent.Show)
            }
            runCatching {
                block()
            }.onSuccess {
                if (loadingEvent == LoadingEvent.Show) {
                    loadingChannel.send(LoadingEvent.Hide)
                }
            }.onFailure { error ->
                if (loadingEvent == LoadingEvent.Show) {
                    loadingChannel.send(LoadingEvent.Hide)
                }
                if (onError != null) {
                    onError(error)
                } else {
                    sendActionableErrorEvent(error, retryAction)
                }
            }
        }
    }

    protected suspend fun sendActionableErrorEvent(error: Throwable, retryAction: (() -> Unit)?) {
        val safeRetryAction = retryAction ?: {}
        val dismissAction = ::onErrorDismissed

        val event = when (error) {
            is UnknownHostException -> ErrorEvent.NetworkError(
                retryAction = safeRetryAction, onDismiss = dismissAction
            )

            is HttpException -> ErrorEvent.HttpError(
                code = error.code(), retryAction = safeRetryAction, onDismiss = dismissAction
            )

            else -> ErrorEvent.UnknownError(
                throwable = error, retryAction = safeRetryAction, onDismiss = dismissAction
            )
        }
        errorEventChannel.send(event)
    }
}