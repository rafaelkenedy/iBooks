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

    protected val errorEventChannel = Channel<ErrorEvent>(Channel.BUFFERED)
    val errorFlow = errorEventChannel.receiveAsFlow()

    protected val loadingChannel = Channel<LoadingEvent>(Channel.BUFFERED)
    val loadingFlow = loadingChannel.receiveAsFlow()

    fun launch(
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
                    handleDefaultErrors(error)
                }
            }
        }
    }

    private suspend fun handleDefaultErrors(error: Throwable) {
        when (error) {
            is UnknownHostException -> errorEventChannel.send(ErrorEvent.NetworkError)
            is HttpException -> errorEventChannel.send(ErrorEvent.HttpError(error.code()))
            else -> errorEventChannel.send(ErrorEvent.UnknownError(error))
        }
    }

}