package com.rafael.ibooks.commons.events

sealed class LoadingEvent {
    object Show : LoadingEvent()
    object Hide : LoadingEvent()
}