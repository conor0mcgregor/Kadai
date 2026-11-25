package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.usecase.buildGroupRanking
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    application: Application,
    initialGroupId: String
) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository
    private val rankingRepository = container.rankingRepository
    private val userRepository = container.userRepository

    private val _state = MutableStateFlow(RankingUiState(selectedGroupId = initialGroupId))
    val state = _state.asStateFlow()

    private var selectedGroupId: String = initialGroupId

    init {
        load()
    }

    fun retry() = load()

    fun selectGroup(groupId: String) {
        if (groupId == selectedGroupId) return
        selectedGroupId = groupId
        _state.update { it.copy(isLoading = true, error = null) }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            runCatching { buildState() }
                .onSuccess { _state.value = it }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo cargar el ranking."
                        )
                    }
                }
        }
    }

    private suspend fun buildState(): RankingUiState {
        val groups = groupRepository.observeGroupsForUser(currentUserId).first()
        val group = groupRepository.getGroup(selectedGroupId)
            ?: error("El grupo ya no está disponible.")
        val memberships = groupRepository.getAllMembershipsForGroup(selectedGroupId)
        val scores = rankingRepository.observeRankingForGroup(selectedGroupId).first()
        val pointsByUser = scores.associate { it.userId to it.totalPoints }

        val memberInfo = memberships.map { membership ->
            val user = userRepository.getUser(membership.userId)
            MemberInfo(
                userId = membership.userId,
                displayName = user?.username ?: "Miembro",
                initial = user?.username?.firstOrNull()?.uppercase() ?: "?",
                isInactive = membership.status == MembershipStatus.INACTIVE,
                isAdmin = membership.role == MemberRole.ADMIN
            )
        }
        val infoByUserId = memberInfo.associateBy { it.userId }
        val orderedIds = memberInfo.sortedBy { it.displayName.lowercase() }.map { it.userId }

        val entries = buildGroupRanking(orderedIds, pointsByUser).map { position ->
            val info = infoByUserId.getValue(position.userId)
            RankingEntry(
                position = position.position,
                userId = position.userId,
                displayName = info.displayName,
                initial = info.initial,
                points = position.points,
                isCurrentUser = position.userId == currentUserId,
                isInactive = info.isInactive,
                isAdmin = info.isAdmin
            )
        }

        return RankingUiState(
            isLoading = false,
            error = null,
            groups = groups.map { RankingGroupOption(it.groupId, it.name, it.photoUrl) },
            selectedGroupId = selectedGroupId,
            groupName = group.name,
            entries = entries
        )
    }

    private data class MemberInfo(
        val userId: String,
        val displayName: String,
        val initial: String,
        val isInactive: Boolean,
        val isAdmin: Boolean
    )
}
