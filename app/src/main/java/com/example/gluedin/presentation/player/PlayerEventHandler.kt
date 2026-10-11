package com.example.gluedin.presentation.player

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.example.gluedin.domain.analytics.PlayerEvent

class PlayerEventHandler(
    private val onEvent: (PlayerEvent) -> Unit
) : Player.Listener {

    private var lastSeekPosition: Long = -1L

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                onEvent(PlayerEvent.Buffering(isBuffering = true))
            }
            Player.STATE_READY -> {
                onEvent(PlayerEvent.Ready)
                onEvent(PlayerEvent.Buffering(isBuffering = false))
            }
            Player.STATE_ENDED -> {
                onEvent(PlayerEvent.Completed)
            }
            Player.STATE_IDLE -> {

            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            onEvent(PlayerEvent.Started)
        } else {
            onEvent(PlayerEvent.Paused)
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            val seekTargetMs = newPosition.positionMs
            if (seekTargetMs != lastSeekPosition) {
                lastSeekPosition = seekTargetMs
                onEvent(PlayerEvent.Seeked(seekTargetMs))
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        val message = error.message ?: "Unknown playback error (code: ${error.errorCode})"
        onEvent(PlayerEvent.Error(message))
    }
}
