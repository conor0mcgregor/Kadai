package com.example.kadai.ui.screens.groups

data class CreateGroupUiState(
    val name: String = "",
    val description: String = "",
    val emoji: String = "🏆",
    val nameError: String? = null,
    val descriptionError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val createdGroupId: String? = null
)
