package com.example.gluedin.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gluedin.data.analytics.AnalyticsRepositoryImpl
import com.example.gluedin.data.datasource.MockContentRemoteDataSourceImpl
import com.example.gluedin.data.repository.ContentRepositoryImpl
import com.example.gluedin.domain.analytics.AnalyticsRepository
import com.example.gluedin.domain.repository.ContentRepository
import com.example.gluedin.domain.usecase.GetContentDetailUseCase
import com.example.gluedin.domain.usecase.GetHomeFeedUseCase
import com.example.gluedin.presentation.detail.DetailViewModel
import com.example.gluedin.presentation.home.HomeViewModel
import com.example.gluedin.presentation.player.PlayerViewModel

object ServiceLocator {

    @Volatile
    private var contentRepository: ContentRepository? = null

    @Volatile
    private var analyticsRepository: AnalyticsRepository? = null

    fun provideContentRepository(context: Context): ContentRepository {
        return contentRepository ?: synchronized(this) {
            contentRepository ?: ContentRepositoryImpl(
                remoteDataSource = MockContentRemoteDataSourceImpl(context.applicationContext)
            ).also { contentRepository = it }
        }
    }

    fun provideAnalyticsRepository(): AnalyticsRepository {
        return analyticsRepository ?: synchronized(this) {
            analyticsRepository ?: AnalyticsRepositoryImpl().also { analyticsRepository = it }
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

    fun provideDetailViewModelFactory(context: Context): ViewModelProvider.Factory {
        val repo = provideContentRepository(context)
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                    return DetailViewModel(GetContentDetailUseCase(repo)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        val analyticsRepo = provideAnalyticsRepository()
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                    return PlayerViewModel(analyticsRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
