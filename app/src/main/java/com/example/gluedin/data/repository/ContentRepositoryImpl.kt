package com.example.gluedin.data.repository

import com.example.gluedin.data.datasource.ContentRemoteDataSource
import com.example.gluedin.data.mapper.toDomain
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.HomeFeed
import com.example.gluedin.domain.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentRepositoryImpl(
    private val remoteDataSource: ContentRemoteDataSource
) : ContentRepository {

    private val cachedItems = mutableMapOf<String, ContentItem>()

    override suspend fun getHomeFeed(): Result<HomeFeed> = withContext(Dispatchers.IO) {
        runCatching {
            val responseDto = remoteDataSource.getHomeFeed()
            val domainFeed = responseDto.toDomain()

            val heroItem = domainFeed.heroBanner.toContentItem()
            cachedItems[heroItem.id] = heroItem

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

            cachedItems[contentId]?.let { return@runCatching it }

            val feed = getHomeFeed().getOrThrow()
            if (feed.heroBanner.id == contentId) {
                return@runCatching feed.heroBanner.toContentItem()
            }

            val foundItem = feed.rails.flatMap { it.items }.find { it.id == contentId }
            foundItem ?: throw NoSuchElementException("Content with id '$contentId' not found.")
        }
    }
}
