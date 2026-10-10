package com.example.gluedin.domain.repository

import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.HomeFeed

/**
 * Domain Repository Interface (Contract).
 *
 * In Clean Architecture, the Domain layer owns the contract.
 * Notice: This is an interface with NO knowledge of Gson, Retrofit, Room, or JSON assets.
 * The Data layer will implement this contract (Dependency Inversion Principle).
 */
interface ContentRepository {
    /**
     * Fetches the full home feed including the hero banner and content rails.
     */
    suspend fun getHomeFeed(): Result<HomeFeed>

    /**
     * Fetches a specific content item by its ID (e.g. for the Detail screen).
     */
    suspend fun getContentDetail(contentId: String): Result<ContentItem>
}
