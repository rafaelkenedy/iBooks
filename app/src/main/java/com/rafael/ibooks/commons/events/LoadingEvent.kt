package com.rafael.ibooks.commons.events

sealed class LoadingEvent {
    data object Show : LoadingEvent()
    data object Hide : LoadingEvent()
}