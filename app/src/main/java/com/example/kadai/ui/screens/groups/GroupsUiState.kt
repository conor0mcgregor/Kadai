package com.example.kadai.ui.screens.groups

data class GroupListEntry(
    val groupId: String,
    val name: String,
    val description: String,
    val emoji: String,
    val memberCount: Int,
    val userPoints: Int
)

data class GroupsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groups: List<GroupListEntry> = emptyList()
)
