package com.example.gluedin.presentation.common


sealed interface UiState<out T> {

    /**
     * Initial or active fetching state.
     * Triggers spinners, progress bars, or shimmer views in the UI.
     */
    data object Loading : UiState<Nothing>

    /**
     * Data was successfully fetched and contains valid content.
     */
    data class Success<out T>(val data: T) : UiState<T>

    /**
     * Request succeeded, but the returned dataset is empty.
     * Triggers an empty state illustration/message.
     */
    data object Empty : UiState<Nothing>

    /**
     * An error occurred (e.g. network timeout, parsing failure).
     * Carries a user-friendly error message and optional retry action.
     */
    data class Error(val message: String) : UiState<Nothing>
}
