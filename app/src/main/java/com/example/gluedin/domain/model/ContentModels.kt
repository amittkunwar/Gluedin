package com.example.gluedin.domain.model

/**
 * Domain Models representing the core business entities of the OTT app.
 * In Clean Architecture, Domain models are pure Kotlin:
 * - They contain non-nullable, sanitized, business-ready values.
 * - They have no annotations (@SerializedName) tying them to an API format.
 * - UI and Use Cases depend solely on these models.
 */

data class HomeFeed(
    val heroBanner: HeroBanner,
    val rails: List<ContentRail>
)

data class HeroBanner(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val bannerUrl: String,
    val videoUrl: String,
    val category: String,
    val duration: String,
    val releaseYear: String,
    val rating: String
) {
    /**
     * Convenience conversion to a standard ContentItem so CTA navigation can be unified.
     */
    fun toContentItem(): ContentItem = ContentItem(
        id = id,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        bannerUrl = bannerUrl,
        videoUrl = videoUrl,
        category = category,
        duration = duration,
        releaseYear = releaseYear,
        rating = rating
    )
}

data class ContentRail(
    val id: String,
    val title: String,
    val items: List<ContentItem>
)

data class ContentItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val bannerUrl: String,
    val videoUrl: String,
    val category: String,
    val duration: String,
    val releaseYear: String,
    val rating: String
) : java.io.Serializable
