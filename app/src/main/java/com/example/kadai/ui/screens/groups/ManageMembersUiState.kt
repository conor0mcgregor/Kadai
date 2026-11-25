package com.example.kadai.ui.screens.groups

data class ManageMemberItem(
    val userId: String,
    val displayName: String,
    val initial: String,
    val roleLabel: String,
    val isCurrentUser: Boolean,
    val isInactive: Boolean,
    val canRemove: Boolean
)

data class ManageMembersUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groupId: String = "",
    val groupName: String = "",
    val isAdmin: Boolean = false,
    val members: List<ManageMemberItem> = emptyList(),
    val actionError: String? = null,
    val actionMessage: String? = null
)
