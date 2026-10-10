package com.example.gluedin.data.mapper

import com.example.gluedin.data.model.dto.ContentItemDto
import com.example.gluedin.data.model.dto.HeroBannerDto
import com.example.gluedin.data.model.dto.HomeFeedResponseDto
import com.example.gluedin.data.model.dto.RailDto
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.ContentRail
import com.example.gluedin.domain.model.HeroBanner
import com.example.gluedin.domain.model.HomeFeed

/**
 * Mapper functions to convert Data Transfer Objects (DTOs) into Domain models.
 * This ensures that:
 * 1. Missing or null values from an API have safe defaults provided here.
 * 2. If the API changes property names or types, only this Mapper changes, protecting
 *    the Domain and Presentation layers from breaking.
 */

fun HomeFeedResponseDto.toDomain(): HomeFeed {
    return HomeFeed(
        heroBanner = heroBanner?.toDomain() ?: HeroBanner(
            id = "",
            title = "",
            description = "",
            thumbnailUrl = "",
            bannerUrl = "",
            videoUrl = "",
            category = "",
            duration = "",
            releaseYear = "",
            rating = ""
        ),
        rails = rails?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}

fun HeroBannerDto.toDomain(): HeroBanner {
    return HeroBanner(
        id = id.orEmpty(),
        title = title.orEmpty(),
        description = description.orEmpty(),
        thumbnailUrl = thumbnailUrl.orEmpty(),
        bannerUrl = bannerUrl.orEmpty(),
        videoUrl = videoUrl.orEmpty(),
        category = category.orEmpty(),
        duration = duration.orEmpty(),
        releaseYear = releaseYear.orEmpty(),
        rating = rating.orEmpty()
    )
}

fun RailDto.toDomain(): ContentRail? {
    val railTitle = railTitle.orEmpty()
    val itemsList = items?.mapNotNull { it.toDomain() } ?: emptyList()
    if (itemsList.isEmpty()) return null
    return ContentRail(
        id = railId.orEmpty(),
        title = railTitle,
        items = itemsList
    )
}

fun ContentItemDto.toDomain(): ContentItem? {
    val itemId = id ?: return null
    return ContentItem(
        id = itemId,
        title = title.orEmpty(),
        description = description.orEmpty(),
        thumbnailUrl = thumbnailUrl.orEmpty(),
        bannerUrl = bannerUrl.orEmpty(),
        videoUrl = videoUrl.orEmpty(),
        category = category.orEmpty(),
        duration = duration.orEmpty(),
        releaseYear = releaseYear.orEmpty(),
        rating = rating.orEmpty()
    )
}
