package com.example.kadai.ui.screens.home

import com.example.kadai.domain.model.MediaType

data class HomeRankingItem(
    val userId: String,
    val displayName: String,
    val initial: String,
    val points: Int
)

data class HomeGroupItem(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val description: String,
    val memberCount: Int,
    val topRanking: List<HomeRankingItem>
)

data class HomeChallengeItem(
    val dailyChallengeId: String,
    val title: String,
    val description: String,
    val category: String,
    val closesAt: Long,
    val participantsText: String,
    val alreadySubmitted: Boolean,
    val isOpen: Boolean
)

data class HomeActivityItem(
    val id: String,
    val userName: String,
    val userInitial: String,
    val action: String,
    val groupName: String,
    val timestamp: String,
    val mediaType: MediaType
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groups: List<HomeGroupItem> = emptyList(),
    val challenge: HomeChallengeItem? = null,
    val activity: List<HomeActivityItem> = emptyList(),
    val unreadNotifications: Int = 0
)
