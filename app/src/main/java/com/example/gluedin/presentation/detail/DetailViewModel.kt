package com.example.gluedin.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.usecase.GetContentDetailUseCase
import com.example.gluedin.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getContentDetailUseCase: GetContentDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ContentItem>>(UiState.Empty)
    val uiState: StateFlow<UiState<ContentItem>> = _uiState.asStateFlow()

    fun loadContentDetail(contentId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getContentDetailUseCase(contentId)
                .onSuccess { item ->
                    _uiState.value = UiState.Success(item)
                }
                .onFailure { exception ->
                    _uiState.value = UiState.Error(
                        message = exception.localizedMessage ?: "Failed to load content details."
                    )
                }
        }
    }
}
