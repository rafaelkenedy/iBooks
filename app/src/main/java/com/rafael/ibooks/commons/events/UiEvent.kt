package com.rafael.ibooks.commons.events

sealed class UiEvent {
    data object NavigateBack : UiEvent()
}