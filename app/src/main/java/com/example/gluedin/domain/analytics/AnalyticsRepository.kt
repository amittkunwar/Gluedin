package com.example.gluedin.domain.analytics

interface AnalyticsRepository {
    fun logPlayerEvent(event: PlayerEvent)
}
