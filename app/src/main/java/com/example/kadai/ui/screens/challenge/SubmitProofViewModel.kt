package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.DailyChallengeStatus
import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.model.Proof
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubmitProofViewModel(application: Application) : KadaiViewModel(application) {

    private val challengeRepository = container.challengeRepository
    private val proofRepository = container.proofRepository

    private val _state = MutableStateFlow(SubmitProofUiState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, loadError = null) }
        load()
    }

    fun onMediaTypeChange(type: MediaType) {
        if (_state.value.isSubmitting) return
        _state.update { current ->
            if (current.mediaType == type) current
            else current.copy(
                mediaType = type,
                pickedLocation = null,
                pickedName = null,
                validationError = null
            )
        }
    }

    fun onMediaPicked(location: String, name: String) {
        _state.update {
            it.copy(
                pickedLocation = location,
                pickedName = name,
                validationError = null,
                submitError = null
            )
        }
    }

    fun onClearMedia() {
        _state.update { it.copy(pickedLocation = null, pickedName = null) }
    }

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return
        if (!current.isOpen) {
            _state.update { it.copy(validationError = "El plazo del reto ya se ha cerrado.") }
            return
        }
        if (current.pickedLocation == null) {
            _state.update {
                it.copy(validationError = "Elige una foto o un vídeo antes de enviar tu prueba.")
            }
            return
        }

        _state.update { it.copy(isSubmitting = true, submitError = null, validationError = null) }
        viewModelScope.launch {
            runCatching {
                val now = System.currentTimeMillis()
                val existing = findMyProof()
                val proof = Proof(
                    proofId = existing?.proofId ?: "proof_${current.dailyChallengeId}_$currentUserId",
                    dailyChallengeId = current.dailyChallengeId,
                    userId = currentUserId,
                    mediaType = current.mediaType,
                    storagePath = requireNotNull(current.pickedLocation),
                    submittedAt = existing?.submittedAt ?: now,
                    updatedAt = now
                )
                proofRepository.submitProof(proof)
            }.onSuccess {
                _state.update { it.copy(isSubmitting = false, submitted = true) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "No se pudo enviar la prueba."
                    )
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            runCatching {
                val daily = challengeRepository.getActiveChallenge()
                    ?: error("No hay ningún reto activo ahora mismo.")
                val challenge = challengeRepository.getChallenge(daily.challengeId)
                val existing = findMyProof()
                SubmitProofUiState(
                    isLoading = false,
                    dailyChallengeId = daily.dailyChallengeId,
                    challengeTitle = challenge?.title ?: "Reto del día",
                    isOpen = daily.status == DailyChallengeStatus.OPEN &&
                        daily.closesAt > System.currentTimeMillis(),
                    mediaType = existing?.mediaType ?: MediaType.PHOTO,
                    pickedLocation = existing?.storagePath,
                    pickedName = existing?.storagePath?.substringAfterLast('/'),
                    isReplacing = existing != null
                )
            }.onSuccess { _state.value = it }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loadError = error.message ?: "No se pudo preparar la entrega."
                        )
                    }
                }
        }
    }

    private suspend fun findMyProof(): Proof? {
        val daily = challengeRepository.getActiveChallenge() ?: return null
        return proofRepository
            .observeProofsForChallenge(daily.dailyChallengeId)
            .first()
            .firstOrNull { it.userId == currentUserId }
    }
}
