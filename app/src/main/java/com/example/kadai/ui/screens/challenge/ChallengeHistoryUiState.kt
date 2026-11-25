package com.example.kadai.ui.screens.challenge

data class HistoryScoreBreakdown(
    val completionPoints: Int = 0,
    val positionPoints: Int = 0,
    val timePoints: Int = 0,
    val totalPoints: Int = 0
)

data class ChallengeHistoryItem(
    val dailyChallengeId: String,
    val title: String,
    val category: String,
    val dateText: String,
    val participantsCount: Int,
    val memberCount: Int,
    val myBreakdown: HistoryScoreBreakdown?,
    val hasValidation: Boolean,
    val isValid: Boolean
)

data class ChallengeHistoryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groupId: String = "",
    val groupName: String = "",
    val items: List<ChallengeHistoryItem> = emptyList()
)
