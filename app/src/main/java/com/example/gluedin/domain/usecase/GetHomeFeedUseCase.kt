package com.example.gluedin.domain.usecase

import com.example.gluedin.domain.model.HomeFeed
import com.example.gluedin.domain.repository.ContentRepository

class GetHomeFeedUseCase(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(): Result<HomeFeed> {
        return contentRepository.getHomeFeed()
    }
}
