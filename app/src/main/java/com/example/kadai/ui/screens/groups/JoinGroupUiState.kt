package com.example.kadai.ui.screens.groups

data class JoinGroupFound(
    val groupId: String,
    val name: String,
    val description: String,
    val photoUrl: String?,
    val memberCount: Int
)

data class JoinGroupUiState(
    val code: String = "",
    val codeError: String? = null,
    val isSearching: Boolean = false,
    val found: JoinGroupFound? = null,
    val isJoining: Boolean = false,
    val joinError: String? = null,
    val joinedGroupId: String? = null
)
