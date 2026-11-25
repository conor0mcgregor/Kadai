package com.example.kadai.ui.screens.proof

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.DailyChallenge
import com.example.kadai.domain.model.DailyChallengeStatus
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.domain.model.Vote
import com.example.kadai.domain.usecase.formatRelativeTime
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProofDetailViewModel(
    application: Application,
    private val proofId: String,
    private val requestedGroupId: String
) : KadaiViewModel(application) {

    private val proofRepository = container.proofRepository
    private val groupRepository = container.groupRepository
    private val userRepository = container.userRepository
    private val challengeRepository = container.challengeRepository

    private val _state = MutableStateFlow(ProofDetailUiState(proofId = proofId))
    val state = _state.asStateFlow()

    private var resolvedGroupId: String = requestedGroupId

    init {
        load()
    }

    fun retry() = load()

    fun vote(positive: Boolean) {
        val current = _state.value
        if (!current.isOpen || current.isOwn) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val existing = proofRepository
                .observeVotesForProof(proofId, resolvedGroupId)
                .first()
                .firstOrNull { it.userId == currentUserId }
            proofRepository.castVote(
                Vote(
                    voteId = existing?.voteId ?: "vote_${proofId}_${resolvedGroupId}_$currentUserId",
                    proofId = proofId,
                    groupId = resolvedGroupId,
                    userId = currentUserId,
                    isPositive = positive,
                    createdAt = existing?.createdAt ?: now,
                    updatedAt = now
                )
            )
            load()
        }
    }

    fun toggleReaction(type: ReactionType) {
        viewModelScope.launch {
            proofRepository.toggleReaction(currentUserId, proofId, type)
            load()
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            proofRepository.toggleFavorite(currentUserId, proofId)
            load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            runCatching { buildState() }
                .onSuccess { _state.value = it }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo cargar la prueba."
                        )
                    }
                }
        }
    }

    private suspend fun buildState(): ProofDetailUiState {
        val proof = proofRepository.getProof(proofId)
            ?: error("La prueba ya no está disponible.")
        val daily = findDailyChallenge(proof.dailyChallengeId)
            ?: error("No se encontró el reto de esta prueba.")
        val challenge = challengeRepository.getChallenge(daily.challengeId)
        val groups = groupRepository.observeGroupsForUser(currentUserId).first()
        val group = resolveGroup(groups, proof.userId)
            ?: error("No compartes ningún grupo con el autor de esta prueba.")
        resolvedGroupId = group.groupId

        val votes = proofRepository.observeVotesForProof(proofId, group.groupId).first()
        val validation = proofRepository.getValidationForGroup(proofId, group.groupId)
        val author = userRepository.getUser(proof.userId)
        val reactions = proofRepository.observeReactionsForProof(proofId).first()
        val favorites = proofRepository.observeFavoriteProofIds(currentUserId).first()

        return ProofDetailUiState(
            isLoading = false,
            proofId = proof.proofId,
            groupId = group.groupId,
            groupName = group.name,
            challengeTitle = challenge?.title ?: "Reto del día",
            authorName = author?.username ?: "Miembro del grupo",
            authorInitial = author?.username?.firstOrNull()?.uppercase() ?: "?",
            mediaType = proof.mediaType,
            storagePath = proof.storagePath,
            submittedAtText = formatRelativeTime(proof.submittedAt),
            isOwn = proof.userId == currentUserId,
            isOpen = daily.status == DailyChallengeStatus.OPEN &&
                daily.closesAt > System.currentTimeMillis(),
            positiveVotes = votes.count { it.isPositive },
            negativeVotes = votes.count { !it.isPositive },
            userVote = votes.firstOrNull { it.userId == currentUserId }?.isPositive,
            isPending = validation == null || validation.closedAt == null,
            isValid = validation?.isValid == true,
            reactions = ReactionType.entries.associateWith { type ->
                reactions.count { it.type == type }
            },
            myReactions = reactions
                .filter { it.userId == currentUserId }
                .map { it.type }
                .toSet(),
            isFavorite = proofId in favorites
        )
    }

    private suspend fun resolveGroup(groups: List<Group>, authorId: String): Group? {
        if (requestedGroupId.isNotBlank()) {
            return groups.firstOrNull { it.groupId == requestedGroupId }
                ?: groupRepository.getGroup(requestedGroupId)
        }
        return groups.firstOrNull { group ->
            groupRepository.getMembershipsForGroup(group.groupId).any { it.userId == authorId }
        }
    }

    private suspend fun findDailyChallenge(dailyChallengeId: String): DailyChallenge? {
        val active = challengeRepository.getActiveChallenge()
        if (active?.dailyChallengeId == dailyChallengeId) return active
        return challengeRepository.getChallengeHistory()
            .firstOrNull { it.dailyChallengeId == dailyChallengeId }
    }
}
