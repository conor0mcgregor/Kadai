package com.example.kadai.ui.screens.home

import com.example.kadai.domain.model.MediaType

data class HomeGroupItem(
    val id: String,
    val name: String,
    val emoji: String,
    val memberCount: Int,
    val userPoints: Int,
    val isActive: Boolean = false
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
    val userName: String = "",
    val userInitial: String = "",
    val userPoints: Int = 0,
    val userRankPosition: Int? = null,
    val challengesCount: Int = 0,
    val streakDays: Int = 0,
    val groups: List<HomeGroupItem> = emptyList(),
    val challenge: HomeChallengeItem? = null,
    val activity: List<HomeActivityItem> = emptyList(),
    val unreadNotifications: Int = 0
)
