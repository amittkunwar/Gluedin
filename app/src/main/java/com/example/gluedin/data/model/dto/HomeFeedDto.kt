package com.example.gluedin.data.model.dto

import com.google.gson.annotations.SerializedName


data class HomeFeedResponseDto(
    @SerializedName("heroBanner")
    val heroBanner: HeroBannerDto?,
    @SerializedName("rails")
    val rails: List<RailDto>?
)

data class HeroBannerDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerializedName("bannerUrl")
    val bannerUrl: String?,
    @SerializedName("videoUrl")
    val videoUrl: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("releaseYear")
    val releaseYear: String?,
    @SerializedName("rating")
    val rating: String?
)

data class RailDto(
    @SerializedName("railId")
    val railId: String?,
    @SerializedName("railTitle")
    val railTitle: String?,
    @SerializedName("items")
    val items: List<ContentItemDto>?
)

data class ContentItemDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerializedName("bannerUrl")
    val bannerUrl: String?,
    @SerializedName("videoUrl")
    val videoUrl: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("releaseYear")
    val releaseYear: String?,
    @SerializedName("rating")
    val rating: String?
)
