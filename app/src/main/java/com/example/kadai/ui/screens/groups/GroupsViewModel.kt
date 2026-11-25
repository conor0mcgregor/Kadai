package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.Group
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupsViewModel(application: Application) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository
    private val rankingRepository = container.rankingRepository

    private val _state = MutableStateFlow(GroupsUiState())
    val state = _state.asStateFlow()

    init {
        observe()
    }

    fun retry() {
        _state.value = GroupsUiState()
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            groupRepository.observeGroupsForUser(currentUserId)
                .collect { groups ->
                    runCatching { buildState(groups) }
                        .onSuccess { _state.value = it }
                        .onFailure { error ->
                            _state.value = GroupsUiState(
                                isLoading = false,
                                error = error.message ?: "No se pudieron cargar tus grupos."
                            )
                        }
                }
        }
    }

    private suspend fun buildState(groups: List<Group>): GroupsUiState {
        val entries = groups.map { group ->
            GroupListEntry(
                groupId = group.groupId,
                name = group.name,
                description = group.description,
                photoUrl = group.photoUrl,
                memberCount = groupRepository.getMembershipsForGroup(group.groupId).size,
                userPoints = rankingRepository.getTotalPointsForUser(currentUserId, group.groupId)
            )
        }
        return GroupsUiState(isLoading = false, error = null, groups = entries)
    }
}
