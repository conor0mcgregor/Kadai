package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.usecase.uniqueInviteCode
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    application: Application,
    private val groupId: String
) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository
    private val userRepository = container.userRepository

    private val _state = MutableStateFlow(GroupDetailUiState(groupId = groupId))
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, error = null) }
        load()
    }

    fun leaveGroup() {
        if (_state.value.isWorking) return
        if (_state.value.isAdmin) {
            _state.update {
                it.copy(actionError = "Como administrador no puedes abandonar el grupo. Elimínalo si ya no lo necesitas.")
            }
            return
        }
        _state.update { it.copy(isWorking = true, actionError = null) }
        viewModelScope.launch {
            runCatching {
                groupRepository.removeMember(groupId, currentUserId)
            }.onSuccess {
                _state.update { it.copy(isWorking = false, leftGroup = true) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isWorking = false,
                        actionError = error.message ?: "No se pudo abandonar el grupo."
                    )
                }
            }
        }
    }

    fun deleteGroup() {
        if (_state.value.isWorking) return
        if (!_state.value.isAdmin) return
        _state.update { it.copy(isWorking = true, actionError = null) }
        viewModelScope.launch {
            runCatching { groupRepository.deleteGroup(groupId) }
                .onSuccess { _state.update { it.copy(isWorking = false, deletedGroup = true) } }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isWorking = false,
                            actionError = error.message ?: "No se pudo eliminar el grupo."
                        )
                    }
                }
        }
    }

    fun updateGroup(name: String, description: String, emoji: String, onDone: () -> Unit) {
        val trimmedName = name.trim()
        if (trimmedName.length < 3) {
            _state.update { it.copy(actionError = "El nombre debe tener al menos 3 caracteres.") }
            return
        }
        _state.update { it.copy(isWorking = true, actionError = null) }
        viewModelScope.launch {
            runCatching {
                val group = groupRepository.getGroup(groupId)
                    ?: error("El grupo ya no está disponible.")
                val existingCodes = groupRepository.getAllGroups()
                    .filter { it.groupId != groupId }
                    .map { it.inviteCode }
                groupRepository.updateGroup(
                    group.copy(
                        name = trimmedName,
                        description = description.trim(),
                        emoji = emoji,
                        inviteCode = group.inviteCode.ifBlank {
                            uniqueInviteCode(trimmedName, existingCodes)
                        }
                    )
                )
            }.onSuccess {
                _state.update { it.copy(isWorking = false) }
                load()
                onDone()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isWorking = false,
                        actionError = error.message ?: "No se pudo actualizar el grupo."
                    )
                }
            }
        }
    }

    fun clearActionError() {
        _state.update { it.copy(actionError = null) }
    }

    private fun load() {
        viewModelScope.launch {
            runCatching { buildState() }
                .onSuccess { _state.value = it }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo cargar el grupo."
                        )
                    }
                }
        }
    }

    private suspend fun buildState(): GroupDetailUiState {
        val group = groupRepository.getGroup(groupId)
            ?: error("El grupo ya no está disponible.")
        val memberships = groupRepository.getAllMembershipsForGroup(groupId)
        val myMembership = memberships.firstOrNull { it.userId == currentUserId }
        val members = memberships.map { membership ->
            val user = userRepository.getUser(membership.userId)
            GroupMemberItem(
                userId = membership.userId,
                displayName = user?.username ?: "Miembro",
                initial = user?.username?.firstOrNull()?.uppercase() ?: "?",
                roleLabel = if (membership.role == MemberRole.ADMIN) "Admin" else "Miembro",
                isCurrentUser = membership.userId == currentUserId,
                isInactive = membership.status == MembershipStatus.INACTIVE
            )
        }
        return GroupDetailUiState(
            isLoading = false,
            error = null,
            groupId = group.groupId,
            name = group.name,
            description = group.description,
            emoji = group.emoji,
            inviteCode = group.inviteCode,
            isAdmin = myMembership?.role == MemberRole.ADMIN,
            members = members,
            memberCount = members.count { !it.isInactive }
        )
    }
}
