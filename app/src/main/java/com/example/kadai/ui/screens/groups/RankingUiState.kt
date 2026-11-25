package com.example.kadai.ui.screens.groups

data class RankingGroupOption(
    val groupId: String,
    val name: String,
    val emoji: String
)

data class RankingEntry(
    val position: Int,
    val userId: String,
    val displayName: String,
    val initial: String,
    val points: Int,
    val isCurrentUser: Boolean,
    val isInactive: Boolean,
    val isAdmin: Boolean
)

data class RankingUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groups: List<RankingGroupOption> = emptyList(),
    val selectedGroupId: String = "",
    val groupName: String = "",
    val entries: List<RankingEntry> = emptyList()
)
