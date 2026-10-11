package com.example.gluedin.domain.repository

import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.HomeFeed

interface ContentRepository {

    suspend fun getHomeFeed(): Result<HomeFeed>

    suspend fun getContentDetail(contentId: String): Result<ContentItem>
}
