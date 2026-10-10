package com.example.gluedin.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gluedin.data.datasource.MockContentRemoteDataSourceImpl
import com.example.gluedin.data.repository.ContentRepositoryImpl
import com.example.gluedin.domain.repository.ContentRepository
import com.example.gluedin.domain.usecase.GetContentDetailUseCase
import com.example.gluedin.domain.usecase.GetHomeFeedUseCase
import com.example.gluedin.presentation.home.HomeViewModel

/**
 * Service Locator / Dependency Container providing singleton instances of repositories
 * and factory functions for ViewModels without needing full Dagger/Hilt setup for a mini exercise.
 */
object ServiceLocator {

    @Volatile
    private var repository: ContentRepository? = null

    fun provideContentRepository(context: Context): ContentRepository {
        return repository ?: synchronized(this) {
            repository ?: ContentRepositoryImpl(
                remoteDataSource = MockContentRemoteDataSourceImpl(context.applicationContext)
            ).also { repository = it }
        }
    }

    fun provideHomeViewModelFactory(context: Context): ViewModelProvider.Factory {
        val repo = provideContentRepository(context)
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return HomeViewModel(GetHomeFeedUseCase(repo)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
