package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.DailyChallenge
import com.example.kadai.domain.model.DailyChallengeStatus
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.Proof
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChallengeDetailViewModel(
    application: Application,
    private val dailyChallengeId: String
) : KadaiViewModel(application) {

    private val challengeRepository = container.challengeRepository
    private val proofRepository = container.proofRepository
    private val groupRepository = container.groupRepository

    private val _state = MutableStateFlow(ChallengeDetailUiState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    init {
        load()
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, error = null) }
        load()
    }

    private fun load() {
        job?.cancel()
        job = viewModelScope.launch {
            combine(
                challengeRepository.observeActiveChallenge(),
                groupRepository.observeGroupsForUser(currentUserId),
                proofRepository.observeProofsForChallenge(dailyChallengeId)
            ) { daily, groups, proofs -> Triple(daily, groups, proofs) }
                .collect { (active, groups, proofs) ->
                    runCatching { buildState(active, groups, proofs) }
                        .onSuccess { _state.value = it }
                        .onFailure { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: "No se pudo cargar el reto."
                                )
                            }
                        }
                }
        }
    }

    private suspend fun buildState(
        active: DailyChallenge?,
        groups: List<Group>,
        proofs: List<Proof>
    ): ChallengeDetailUiState {
        val daily = when {
            active?.dailyChallengeId == dailyChallengeId -> active
            else -> challengeRepository.getChallengeHistory()
                .firstOrNull { it.dailyChallengeId == dailyChallengeId }
        } ?: error("El reto solicitado ya no está disponible.")
        val content = challengeRepository.getChallenge(daily.challengeId)
            ?: error("No se encontró el contenido del reto.")
        val primaryGroup = groups.firstOrNull()
        val myProof = proofs.firstOrNull { it.userId == currentUserId }
        val memberIds = primaryGroup
            ?.let { groupRepository.getMembershipsForGroup(it.groupId).map { member -> member.userId } }
            .orEmpty()
        val participants = proofs.count { it.userId in memberIds }

        return ChallengeDetailUiState(
            isLoading = false,
            dailyChallengeId = daily.dailyChallengeId,
            title = content.title,
            description = content.description,
            category = content.category,
            publishedAt = daily.publishedAt,
            closesAt = daily.closesAt,
            isOpen = daily.status == DailyChallengeStatus.OPEN &&
                daily.closesAt > System.currentTimeMillis(),
            alreadySubmitted = myProof != null,
            myProofId = myProof?.proofId,
            groups = groups.map {
                ChallengeGroupOption(it.groupId, it.name)
            },
            primaryGroupId = primaryGroup?.groupId,
            participantsCount = participants,
            memberCount = memberIds.size
        )
    }
}
