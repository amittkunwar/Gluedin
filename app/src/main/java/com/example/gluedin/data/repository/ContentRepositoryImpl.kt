package com.example.gluedin.data.repository

import com.example.gluedin.data.datasource.ContentRemoteDataSource
import com.example.gluedin.data.mapper.toDomain
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.HomeFeed
import com.example.gluedin.domain.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data Layer implementation of the domain [ContentRepository] contract.
 *
 * Responsibilities:
 * 1. Fetches raw DTOs from [ContentRemoteDataSource].
 * 2. Maps DTOs to Domain models using [toDomain].
 * 3. In-memory cache for fast detail screen lookups.
 * 4. Wraps results in [Result] to safely handle exceptions.
 */
class ContentRepositoryImpl(
    private val remoteDataSource: ContentRemoteDataSource
) : ContentRepository {

    // Simple in-memory cache of items for instant Detail screen resolution
    private val cachedItems = mutableMapOf<String, ContentItem>()

    override suspend fun getHomeFeed(): Result<HomeFeed> = withContext(Dispatchers.IO) {
        runCatching {
            val responseDto = remoteDataSource.getHomeFeed()
            val domainFeed = responseDto.toDomain()

            // Cache hero item
            val heroItem = domainFeed.heroBanner.toContentItem()
            cachedItems[heroItem.id] = heroItem

            // Cache all rail items
            domainFeed.rails.forEach { rail ->
                rail.items.forEach { item ->
                    cachedItems[item.id] = item
                }
            }

            domainFeed
        }
    }

    override suspend fun getContentDetail(contentId: String): Result<ContentItem> = withContext(Dispatchers.IO) {
        runCatching {
            // First check in-memory cache
            cachedItems[contentId]?.let { return@runCatching it }

            // If not found in cache (e.g. direct deep link), fetch feed and look up
            val feed = getHomeFeed().getOrThrow()
            if (feed.heroBanner.id == contentId) {
                return@runCatching feed.heroBanner.toContentItem()
            }

            val foundItem = feed.rails.flatMap { it.items }.find { it.id == contentId }
            foundItem ?: throw NoSuchElementException("Content with id '$contentId' not found.")
        }
    }
}
