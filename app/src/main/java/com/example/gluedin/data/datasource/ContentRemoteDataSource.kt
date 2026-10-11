package com.example.gluedin.data.datasource

import com.example.gluedin.data.model.dto.HomeFeedResponseDto

interface ContentRemoteDataSource {
    suspend fun getHomeFeed(): HomeFeedResponseDto
}
