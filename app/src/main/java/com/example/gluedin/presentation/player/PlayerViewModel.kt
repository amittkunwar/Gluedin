package com.example.gluedin.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gluedin.domain.analytics.AnalyticsRepository
import com.example.gluedin.domain.analytics.PlayerEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isReady: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val errorMessage: String? = null,
    val lastLoggedEvent: String = "INITIALIZING"
)

class PlayerViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<PlayerEvent>()
    val eventFlow: SharedFlow<PlayerEvent> = _eventFlow.asSharedFlow()

    fun onPlayerEvent(event: PlayerEvent) {

        analyticsRepository.logPlayerEvent(event)

        val current = _uiState.value
        _uiState.value = when (event) {
            is PlayerEvent.Ready -> current.copy(
                isReady = true,
                isBuffering = false,
                lastLoggedEvent = "PLAYER_READY"
            )
            is PlayerEvent.Started -> current.copy(
                isPlaying = true,
                isBuffering = false,
                lastLoggedEvent = "PLAYER_STARTED"
            )
            is PlayerEvent.Paused -> current.copy(
                isPlaying = false,
                lastLoggedEvent = "PLAYER_PAUSED"
            )
            is PlayerEvent.Seeked -> current.copy(
                currentPositionMs = event.positionMs,
                lastLoggedEvent = "PLAYER_SEEKED (${event.positionMs / 1000}s)"
            )
            is PlayerEvent.Buffering -> current.copy(
                isBuffering = event.isBuffering,
                lastLoggedEvent = if (event.isBuffering) "BUFFERING_STARTED" else "BUFFERING_ENDED"
            )
            is PlayerEvent.Completed -> current.copy(
                isPlaying = false,
                lastLoggedEvent = "PLAYER_COMPLETED"
            )
            is PlayerEvent.Error -> current.copy(
                errorMessage = event.errorMessage,
                isBuffering = false,
                lastLoggedEvent = "PLAYER_ERROR"
            )
        }

        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun updateProgress(currentMs: Long, durationMs: Long) {
        _uiState.value = _uiState.value.copy(
            currentPositionMs = currentMs,
            durationMs = durationMs
        )
    }
}
