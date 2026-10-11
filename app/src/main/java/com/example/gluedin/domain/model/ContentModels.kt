package com.example.gluedin.domain.model

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
