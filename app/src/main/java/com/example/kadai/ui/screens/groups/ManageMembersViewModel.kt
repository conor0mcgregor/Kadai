package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageMembersViewModel(
    application: Application,
    private val groupId: String
) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository
    private val userRepository = container.userRepository

    private val _state = MutableStateFlow(ManageMembersUiState(groupId = groupId))
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, error = null) }
        load()
    }

    fun removeMember(userId: String) {
        if (!_state.value.isAdmin) return
        if (userId == currentUserId) {
            _state.update { it.copy(actionError = "No puedes expulsarte a ti mismo.") }
            return
        }
        viewModelScope.launch {
            runCatching { groupRepository.removeMember(groupId, userId) }
                .onSuccess {
                    _state.update { it.copy(actionError = null, actionMessage = "Miembro expulsado.") }
                    load()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(actionError = error.message ?: "No se pudo expulsar al miembro.")
                    }
                }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(actionError = null, actionMessage = null) }
    }

    private fun load() {
        viewModelScope.launch {
            runCatching { buildState() }
                .onSuccess { built ->
                    _state.update {
                        built.copy(
                            actionError = it.actionError,
                            actionMessage = it.actionMessage
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudieron cargar los miembros."
                        )
                    }
                }
        }
    }

    private suspend fun buildState(): ManageMembersUiState {
        val group = groupRepository.getGroup(groupId)
            ?: error("El grupo ya no está disponible.")
        val memberships = groupRepository.getAllMembershipsForGroup(groupId)
        val myMembership = memberships.firstOrNull { it.userId == currentUserId }
        val isAdmin = myMembership?.role == MemberRole.ADMIN

        val members = memberships.map { membership ->
            val user = userRepository.getUser(membership.userId)
            val isInactive = membership.status == MembershipStatus.INACTIVE
            ManageMemberItem(
                userId = membership.userId,
                displayName = user?.username ?: "Miembro",
                initial = user?.username?.firstOrNull()?.uppercase() ?: "?",
                roleLabel = if (membership.role == MemberRole.ADMIN) "Admin" else "Miembro",
                isCurrentUser = membership.userId == currentUserId,
                isInactive = isInactive,
                canRemove = isAdmin &&
                    !isInactive &&
                    membership.userId != currentUserId &&
                    membership.role != MemberRole.ADMIN
            )
        }

        return ManageMembersUiState(
            isLoading = false,
            error = null,
            groupId = groupId,
            groupName = group.name,
            isAdmin = isAdmin,
            members = members
        )
    }
}
