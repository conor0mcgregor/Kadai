package com.example.kadai.ui.screens.groups

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InviteViewModel(
    application: Application,
    private val groupId: String
) : KadaiViewModel(application) {

    private val groupRepository = container.groupRepository

    private val _state = MutableStateFlow(InviteUiState(groupId = groupId))
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, error = null) }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            runCatching {
                val group = groupRepository.getGroup(groupId)
                    ?: error("El grupo ya no está disponible.")
                InviteUiState(
                    isLoading = false,
                    groupId = group.groupId,
                    groupName = group.name,
                    photoUrl = group.photoUrl,
                    inviteCode = group.inviteCode
                )
            }.onSuccess { _state.value = it }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo preparar la invitación."
                        )
                    }
                }
        }
    }
}
