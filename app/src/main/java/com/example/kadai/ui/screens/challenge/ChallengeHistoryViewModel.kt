package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.usecase.formatShortDate
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChallengeHistoryViewModel(
    application: Application,
    private val groupId: String
) : KadaiViewModel(application) {

    private val challengeRepository = container.challengeRepository
    private val rankingRepository = container.rankingRepository
    private val proofRepository = container.proofRepository
    private val groupRepository = container.groupRepository

    private val _state = MutableStateFlow(ChallengeHistoryUiState(groupId = groupId))
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
            runCatching { buildState() }
                .onSuccess { _state.value = it }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo cargar el historial."
                        )
                    }
                }
        }
    }

    private suspend fun buildState(): ChallengeHistoryUiState {
        val group = groupRepository.getGroup(groupId)
            ?: error("El grupo ya no está disponible.")
        val memberIds = groupRepository.getMembershipsForGroup(groupId).map { it.userId }.toSet()
        val history = challengeRepository.getChallengeHistory()
            .sortedByDescending { it.publishedAt }

        val items = history.map { daily ->
            val challenge = challengeRepository.getChallenge(daily.challengeId)
            val proofs = proofRepository.observeProofsForChallenge(daily.dailyChallengeId).first()
            val groupProofs = proofs.filter { it.userId in memberIds }
            val myProof = proofs.firstOrNull { it.userId == currentUserId }
            val validation = myProof?.let {
                proofRepository.getValidationForGroup(it.proofId, groupId)
            }
            val scores = rankingRepository.getScoresForChallenge(daily.dailyChallengeId, groupId)
            val mine = scores.firstOrNull { it.userId == currentUserId }

            ChallengeHistoryItem(
                dailyChallengeId = daily.dailyChallengeId,
                title = challenge?.title ?: "Reto del día",
                category = challenge?.category.orEmpty(),
                dateText = formatShortDate(daily.publishedAt),
                participantsCount = groupProofs.size,
                memberCount = memberIds.size,
                myBreakdown = mine?.let {
                    HistoryScoreBreakdown(
                        completionPoints = it.completionPoints,
                        positionPoints = it.positionPoints,
                        timePoints = it.timePoints,
                        totalPoints = it.totalPoints
                    )
                },
                hasValidation = validation != null,
                isValid = validation?.isValid == true
            )
        }

        return ChallengeHistoryUiState(
            isLoading = false,
            groupId = groupId,
            groupName = group.name,
            items = items
        )
    }
}
