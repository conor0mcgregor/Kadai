package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.Membership
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.usecase.normalizeInviteCode
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JoinGroupViewModel(application: Application) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository

    private val _state = MutableStateFlow(JoinGroupUiState())
    val state = _state.asStateFlow()

    private var foundGroup: Group? = null

    fun onCodeChange(value: String) {
        foundGroup = null
        _state.update {
            it.copy(
                code = value.uppercase(),
                codeError = null,
                found = null,
                joinError = null
            )
        }
    }

    fun search() {
        val normalized = normalizeInviteCode(_state.value.code)
        if (normalized.isBlank()) {
            _state.update { it.copy(codeError = "Escribe un código de invitación.") }
            return
        }
        _state.update { it.copy(isSearching = true, codeError = null, joinError = null) }
        viewModelScope.launch {
            runCatching {
                val group = groupRepository.getGroupByInviteCode(normalized)
                    ?: error("No existe ningún grupo con el código $normalized.")
                if (isMember(group.groupId)) {
                    error("Ya perteneces a este grupo.")
                }
                group
            }.onSuccess { group ->
                foundGroup = group
                _state.update {
                    it.copy(
                        isSearching = false,
                        code = group.inviteCode,
                        found = JoinGroupFound(
                            groupId = group.groupId,
                            name = group.name,
                            description = group.description,
                            photoUrl = group.photoUrl,
                            memberCount = groupRepository.getMembershipsForGroup(group.groupId).size
                        )
                    )
                }
            }.onFailure { error ->
                foundGroup = null
                _state.update {
                    it.copy(
                        isSearching = false,
                        found = null,
                        codeError = error.message ?: "No se pudo buscar el grupo."
                    )
                }
            }
        }
    }

    fun join() {
        val group = foundGroup ?: return
        if (_state.value.isJoining) return
        _state.update { it.copy(isJoining = true, joinError = null) }
        viewModelScope.launch {
            runCatching {
                groupRepository.addMember(
                    Membership(
                        groupId = group.groupId,
                        userId = currentUserId,
                        role = MemberRole.MEMBER,
                        status = MembershipStatus.ACTIVE,
                        joinedAt = System.currentTimeMillis()
                    )
                )
                group.groupId
            }.onSuccess { groupId ->
                _state.update { it.copy(isJoining = false, joinedGroupId = groupId) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isJoining = false,
                        joinError = error.message ?: "No se pudo unir al grupo."
                    )
                }
            }
        }
    }

    private suspend fun isMember(groupId: String): Boolean =
        groupRepository.getMembershipsForUser(currentUserId)
            .any { it.groupId == groupId }
}
