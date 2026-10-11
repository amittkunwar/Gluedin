package com.example.gluedin.data.analytics

import android.util.Log
import com.example.gluedin.domain.analytics.AnalyticsRepository
import com.example.gluedin.domain.analytics.PlayerEvent

class AnalyticsRepositoryImpl : AnalyticsRepository {

    companion object {
        private const val TAG = "OTT_PLAYER_ANALYTICS"
    }

    override fun logPlayerEvent(event: PlayerEvent) {
        val logMessage = when (event) {
            is PlayerEvent.Ready -> "PLAYER_READY"
            is PlayerEvent.Started -> "PLAYER_STARTED"
            is PlayerEvent.Paused -> "PLAYER_PAUSED"
            is PlayerEvent.Seeked -> "PLAYER_SEEKED (position: ${event.positionMs}ms)"
            is PlayerEvent.Buffering -> "PLAYER_BUFFERING (buffering: ${event.isBuffering})"
            is PlayerEvent.Completed -> "PLAYER_COMPLETED"
            is PlayerEvent.Error -> "PLAYER_ERROR (error: ${event.errorMessage})"
        }
        Log.i(TAG, "===> [EVENT] $logMessage")
    }
}
