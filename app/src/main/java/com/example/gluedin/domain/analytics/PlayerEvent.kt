package com.example.gluedin.domain.analytics

sealed interface PlayerEvent {

    data object Ready : PlayerEvent

    data object Started : PlayerEvent

    data object Paused : PlayerEvent

    data class Seeked(val positionMs: Long) : PlayerEvent

    data class Buffering(val isBuffering: Boolean) : PlayerEvent

    data object Completed : PlayerEvent

    data class Error(val errorMessage: String) : PlayerEvent
}
