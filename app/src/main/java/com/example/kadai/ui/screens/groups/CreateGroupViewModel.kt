package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.GroupStatus
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.Membership
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.usecase.uniqueInviteCode
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateGroupViewModel(application: Application) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository

    private val _state = MutableStateFlow(CreateGroupUiState())
    val state = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value, nameError = null, submitError = null) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value, descriptionError = null, submitError = null) }
    }

    fun onEmojiChange(value: String) {
        _state.update { it.copy(emoji = value) }
    }

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val name = current.name.trim()
        val description = current.description.trim()
        val nameError = when {
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres."
            name.length > 30 -> "El nombre no puede superar los 30 caracteres."
            else -> null
        }
        val descriptionError = if (description.length > 120) {
            "La descripción no puede superar los 120 caracteres."
        } else null

        if (nameError != null || descriptionError != null) {
            _state.update {
                it.copy(nameError = nameError, descriptionError = descriptionError)
            }
            return
        }

        _state.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            runCatching {
                val existingCodes = groupRepository.getAllGroups().map { it.inviteCode }
                val groupId = "group_${System.currentTimeMillis()}"
                val group = Group(
                    groupId = groupId,
                    name = name,
                    description = description,
                    adminId = currentUserId,
                    createdAt = System.currentTimeMillis(),
                    status = GroupStatus.ACTIVE,
                    emoji = current.emoji,
                    inviteCode = uniqueInviteCode(name, existingCodes)
                )
                groupRepository.createGroup(
                    group = group,
                    adminMembership = Membership(
                        groupId = groupId,
                        userId = currentUserId,
                        role = MemberRole.ADMIN,
                        status = MembershipStatus.ACTIVE,
                        joinedAt = System.currentTimeMillis()
                    )
                )
                groupId
            }.onSuccess { groupId ->
                _state.update { it.copy(isSubmitting = false, createdGroupId = groupId) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "No se pudo crear el grupo."
                    )
                }
            }
        }
    }
}
