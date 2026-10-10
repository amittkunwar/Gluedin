package com.example.gluedin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gluedin.domain.model.HomeFeed
import com.example.gluedin.domain.usecase.GetHomeFeedUseCase
import com.example.gluedin.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the OTT Home Screen.
 *
 * Responsibilities:
 * 1. Invokes [GetHomeFeedUseCase] to retrieve home feed content.
 * 2. Emits state via [StateFlow] adhering to Unidirectional Data Flow (UDF).
 * 3. Survives Android configuration changes (screen rotations, splits).
 */
class HomeViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeFeed>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeFeed>> = _uiState.asStateFlow()

    init {
        loadHomeFeed()
    }

    /**
     * Fetches the home feed data, transitioning through Loading -> Success/Error/Empty states.
     */
    fun loadHomeFeed() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getHomeFeedUseCase()
                .onSuccess { feed ->
                    if (feed.rails.isEmpty() && feed.heroBanner.id.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(feed)
                    }
                }
                .onFailure { exception ->
                    _uiState.value = UiState.Error(
                        message = exception.localizedMessage ?: "Failed to load home feed. Please check your connection."
                    )
                }
        }
    }
}
