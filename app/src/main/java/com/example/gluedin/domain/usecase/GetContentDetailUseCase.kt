package com.example.gluedin.domain.usecase

import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.repository.ContentRepository

/**
 * Use Case representing the business action: "Fetch Details for a selected Content Item".
 *
 * Encapsulates the detail-fetching operation so DetailViewModel remains decoupled from the
 * underlying data storage/networking implementation.
 */
class GetContentDetailUseCase(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(contentId: String): Result<ContentItem> {
        return contentRepository.getContentDetail(contentId)
    }
}
