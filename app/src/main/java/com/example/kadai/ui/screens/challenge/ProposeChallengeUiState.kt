package com.example.kadai.ui.screens.challenge

data class ProposeChallengeUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitted: Boolean = false
)
