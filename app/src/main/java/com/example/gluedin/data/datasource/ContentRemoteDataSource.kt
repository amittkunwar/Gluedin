package com.example.gluedin.data.datasource

import com.example.gluedin.data.model.dto.HomeFeedResponseDto

/**
 * Remote Data Source Contract.
 * In production, this interface can be implemented by:
 * 1. MockContentRemoteDataSourceImpl (currently reading from assets/mock_home_feed.json)
 * 2. RetrofitContentRemoteDataSourceImpl (calling a real HTTP endpoint via Retrofit/OkHttp)
 *
 * This provides total decoupling and satisfies requirement #8 (API-Ready Design).
 */
interface ContentRemoteDataSource {
    suspend fun getHomeFeed(): HomeFeedResponseDto
}
