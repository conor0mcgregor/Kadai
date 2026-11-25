package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.model.Score
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

        val ordered = memberships
            .map { membership ->
                val user = userRepository.getUser(membership.userId)
                RankingRow(
                    userId = membership.userId,
                    displayName = user?.username ?: "Miembro",
                    initial = user?.username?.firstOrNull()?.uppercase() ?: "?",
                    points = pointsByUser[membership.userId] ?: 0,
                    isInactive = membership.status == MembershipStatus.INACTIVE,
                    isAdmin = membership.role == MemberRole.ADMIN
                )
            }
            .sortedWith(compareByDescending<RankingRow> { it.points }.thenBy { it.displayName })

        var previousPoints: Int? = null
        var previousPosition = 0
        val entries = ordered.mapIndexed { index, row ->
            val position = if (row.points == previousPoints) previousPosition else index + 1
            previousPoints = row.points
            previousPosition = position
            RankingEntry(
                position = position,
                userId = row.userId,
                displayName = row.displayName,
                initial = row.initial,
                points = row.points,
                isCurrentUser = row.userId == currentUserId,
                isInactive = row.isInactive,
                isAdmin = row.isAdmin
            )
        }

        return RankingUiState(
            isLoading = false,
            error = null,
            groups = groups.map { RankingGroupOption(it.groupId, it.name, it.emoji) },
            selectedGroupId = selectedGroupId,
            groupName = group.name,
            entries = entries
        )
    }

    private data class RankingRow(
        val userId: String,
        val displayName: String,
        val initial: String,
        val points: Int,
        val isInactive: Boolean,
        val isAdmin: Boolean
    )
}
