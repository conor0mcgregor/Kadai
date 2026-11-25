package com.example.kadai.ui.screens.challenge

data class ChallengeGroupOption(
    val groupId: String,
    val name: String,
    val emoji: String
)

data class ChallengeDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val dailyChallengeId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val publishedAt: Long = 0L,
    val closesAt: Long = 0L,
    val isOpen: Boolean = false,
    val alreadySubmitted: Boolean = false,
    val myProofId: String? = null,
    val groups: List<ChallengeGroupOption> = emptyList(),
    val primaryGroupId: String? = null,
    val participantsCount: Int = 0,
    val memberCount: Int = 0
)
