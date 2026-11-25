package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.Challenge
import com.example.kadai.domain.model.ChallengeStatus
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProposeChallengeViewModel(application: Application) : KadaiViewModel(application) {

    private val challengeRepository = container.challengeRepository

    private val _state = MutableStateFlow(ProposeChallengeUiState())
    val state = _state.asStateFlow()

    fun onTitleChange(value: String) {
        _state.update { it.copy(title = value, titleError = null, submitError = null) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value, descriptionError = null, submitError = null) }
    }

    fun onCategoryChange(value: String) {
        _state.update { it.copy(category = value, categoryError = null, submitError = null) }
    }

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val titleError = if (current.title.trim().length < 4) {
            "Escribe un título de al menos 4 caracteres."
        } else null
        val descriptionError = if (current.description.trim().length < 12) {
            "Describe el reto con al menos 12 caracteres."
        } else null
        val categoryError = if (current.category.isBlank()) {
            "Elige una categoría para el reto."
        } else null

        if (titleError != null || descriptionError != null || categoryError != null) {
            _state.update {
                it.copy(
                    titleError = titleError,
                    descriptionError = descriptionError,
                    categoryError = categoryError
                )
            }
            return
        }

        _state.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            runCatching {
                challengeRepository.proposeChallenge(
                    Challenge(
                        challengeId = "proposal_${System.currentTimeMillis()}",
                        title = current.title.trim(),
                        description = current.description.trim(),
                        category = current.category,
                        createdBy = currentUserId,
                        status = ChallengeStatus.PENDING,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }.onSuccess {
                _state.update { it.copy(isSubmitting = false, submitted = true) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "No se pudo enviar la propuesta."
                    )
                }
            }
        }
    }
}
