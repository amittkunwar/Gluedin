package com.example.gluedin.data.datasource

import android.content.Context
import com.example.gluedin.data.model.dto.HomeFeedResponseDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class MockContentRemoteDataSourceImpl(
    private val context: Context,
    private val gson: Gson = Gson(),
    private val simulatedDelayMs: Long = 600L
) : ContentRemoteDataSource {

    override suspend fun getHomeFeed(): HomeFeedResponseDto = withContext(Dispatchers.IO) {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        context.assets.open("mock_home_feed.json").use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                gson.fromJson(reader, HomeFeedResponseDto::class.java)
            }
        } ?: throw IllegalStateException("Failed to parse mock_home_feed.json")
    }
}
