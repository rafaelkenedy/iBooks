package com.rafael.ibooks.commons.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafael.ibooks.commons.events.ErrorEvent
import com.rafael.ibooks.commons.events.LoadingEvent
import com.rafael.ibooks.commons.events.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    private val _errorFlow = MutableSharedFlow<ErrorEvent>()
    val errorFlow = _errorFlow.asSharedFlow()

    private val _loadingFlow = MutableSharedFlow<LoadingEvent>()
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private val onceEventLocks = mutableMapOf<Class<out UiEvent>, Boolean>()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    protected open fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEventFlow.emit(event) }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    protected open fun sendUiEventOnce(event: UiEvent) {
        val key = event::class.java
        if (onceEventLocks[key] == true) return

        onceEventLocks[key] = true
        viewModelScope.launch {
            _uiEventFlow.emit(event)
        }
    }

    open fun onErrorDismissed() {}


    fun launch(
        retryAction: (() -> Unit)? = null,
        onError: (suspend CoroutineScope.(Throwable) -> Unit)? = null,
        loadingEvent: LoadingEvent? = LoadingEvent.Show,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (loadingEvent == LoadingEvent.Show) {
                    _loadingFlow.emit(LoadingEvent.Show)
                }
                block()
            } catch (error: Throwable) {
                if (onError != null) {
                    onError(error)
                } else {
                    sendActionableErrorEvent(error, retryAction)
                }
            } finally {
                if (loadingEvent == LoadingEvent.Show) {
                    _loadingFlow.emit(LoadingEvent.Hide)
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
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
        _errorFlow.emit(event)
    }
}