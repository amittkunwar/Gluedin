package com.example.gluedin.domain.usecase

import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.repository.ContentRepository

class GetContentDetailUseCase(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(contentId: String): Result<ContentItem> {
        return contentRepository.getContentDetail(contentId)
    }
}
