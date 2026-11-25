package com.example.kadai.ui.screens.groups

data class GroupMemberItem(
    val userId: String,
    val displayName: String,
    val initial: String,
    val roleLabel: String,
    val isCurrentUser: Boolean,
    val isInactive: Boolean
)

data class GroupDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groupId: String = "",
    val name: String = "",
    val description: String = "",
    val emoji: String = "🏆",
    val inviteCode: String = "",
    val isAdmin: Boolean = false,
    val members: List<GroupMemberItem> = emptyList(),
    val memberCount: Int = 0,
    val actionError: String? = null,
    val isWorking: Boolean = false,
    val leftGroup: Boolean = false,
    val deletedGroup: Boolean = false
)
