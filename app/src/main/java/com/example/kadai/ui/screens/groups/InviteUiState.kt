package com.example.kadai.ui.screens.groups

data class InviteUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groupId: String = "",
    val groupName: String = "",
    val emoji: String = "🏆",
    val inviteCode: String = ""
)
