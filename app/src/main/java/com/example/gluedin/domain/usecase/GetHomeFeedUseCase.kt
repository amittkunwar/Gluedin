package com.example.gluedin.domain.usecase

import com.example.gluedin.domain.model.HomeFeed
import com.example.gluedin.domain.repository.ContentRepository

/**
 * Use Case representing the business action: "Fetch the Home Feed for the OTT app".
 *
 * In Clean Architecture:
 * - A Use Case has a SINGLE responsibility (Single Responsibility Principle).
 * - It contains business rules (e.g. filtering, sorting, or validating content before presentation).
 * - ViewModels invoke Use Cases, never the Repository directly.
 */
class GetHomeFeedUseCase(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(): Result<HomeFeed> {
        return contentRepository.getHomeFeed()
    }
}
