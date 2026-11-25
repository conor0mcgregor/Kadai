package com.example.kadai.ui.screens.proof

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.DailyChallengeStatus
import com.example.kadai.domain.model.GroupValidation
import com.example.kadai.domain.model.Proof
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.domain.model.Vote
import com.example.kadai.domain.usecase.formatRelativeTime
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupProofsViewModel(
    application: Application,
    private val groupId: String
) : KadaiViewModel(application) {

    private val challengeRepository = container.challengeRepository
    private val proofRepository = container.proofRepository
    private val groupRepository = container.groupRepository
    private val userRepository = container.userRepository

    private val _state = MutableStateFlow(GroupProofsUiState(groupId = groupId))
    val state = _state.asStateFlow()

    private var filter = ProofFilter.ALL
    private var job: Job? = null

    init {
        load()
    }

    fun retry() = load()

    fun setFilter(newFilter: ProofFilter) {
        filter = newFilter
        load()
    }

    fun vote(item: GroupProofItem, positive: Boolean) {
        if (item.isOwn || !_state.value.isOpen) return
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            val existing = existingVote(item.proofId)
            proofRepository.castVote(
                Vote(
                    voteId = existing?.voteId ?: "vote_${item.proofId}_${groupId}_$currentUserId",
                    proofId = item.proofId,
                    groupId = groupId,
                    userId = currentUserId,
                    isPositive = positive,
                    createdAt = existing?.createdAt ?: now,
                    updatedAt = now
                )
            )
        }
    }

    fun toggleReaction(proofId: String, type: ReactionType) {
        viewModelScope.launch {
            proofRepository.toggleReaction(currentUserId, proofId, type)
            load()
        }
    }

    private fun load() {
        job?.cancel()
        job = viewModelScope.launch {
            val daily = challengeRepository.getActiveChallenge()
            val proofsFlow = if (daily == null) flowOf(emptyList()) else {
                proofRepository.observeProofsForChallenge(daily.dailyChallengeId)
            }
            combine(proofsFlow, proofRepository.observeVotesForGroup(groupId)) { proofs, votes ->
                proofs to votes
            }.collect { (proofs, votes) ->
                runCatching { buildState(daily?.dailyChallengeId, proofs, votes) }
                    .onSuccess { _state.value = it }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "No se pudieron cargar las pruebas."
                            )
                        }
                    }
            }
        }
    }

    private suspend fun buildState(
        dailyChallengeId: String?,
        proofs: List<Proof>,
        votes: List<Vote>
    ): GroupProofsUiState {
        val group = groupRepository.getGroup(groupId)
            ?: error("El grupo ya no está disponible.")
        val daily = challengeRepository.getActiveChallenge()
        val challenge = daily?.let { challengeRepository.getChallenge(it.challengeId) }
        val members = groupRepository.getMembershipsForGroup(groupId)
        val memberIds = members.map { it.userId }.toSet()
        val now = System.currentTimeMillis()
        val isOpen = daily?.status == DailyChallengeStatus.OPEN &&
            daily.closesAt > now &&
            daily.dailyChallengeId == dailyChallengeId

        val relevantProofs = proofs
            .filter { it.userId in memberIds }
            .sortedBy { it.submittedAt }

        val items = relevantProofs.map { proof ->
            val proofVotes = votes.filter { it.proofId == proof.proofId }
            val positive = proofVotes.count { it.isPositive }
            val negative = proofVotes.count { !it.isPositive }
            val validation = proofRepository.getValidationForGroup(proof.proofId, groupId)
            val author = userRepository.getUser(proof.userId)
            val reactions = proofRepository.observeReactionsForProof(proof.proofId).first()
            val counts = ReactionType.entries.associateWith { type ->
                reactions.count { it.type == type }
            }
            val mine = reactions.filter { it.userId == currentUserId }.map { it.type }.toSet()

            GroupProofItem(
                proofId = proof.proofId,
                authorName = author?.username ?: "Miembro del grupo",
                authorInitial = author?.username?.firstOrNull()?.uppercase() ?: "?",
                mediaType = proof.mediaType,
                storagePath = proof.storagePath,
                submittedAtText = formatRelativeTime(proof.submittedAt, now),
                positiveVotes = positive,
                negativeVotes = negative,
                userVote = proofVotes.firstOrNull { it.userId == currentUserId }?.isPositive,
                isOwn = proof.userId == currentUserId,
                isPending = isPending(validation),
                isValid = validation?.isValid == true,
                reactions = counts,
                myReactions = mine
            )
        }.filter { item ->
            when (filter) {
                ProofFilter.ALL -> true
                ProofFilter.PENDING -> item.isPending
                ProofFilter.VALID -> !item.isPending && item.isValid
                ProofFilter.REJECTED -> !item.isPending && !item.isValid
            }
        }

        return GroupProofsUiState(
            isLoading = false,
            error = null,
            groupId = groupId,
            groupName = group.name,
            groupPhotoUrl = group.photoUrl,
            challengeTitle = challenge?.title ?: "Reto del día",
            isOpen = isOpen,
            filter = filter,
            items = items,
            memberCount = memberIds.size
        )
    }

    private fun isPending(validation: GroupValidation?): Boolean =
        validation == null || validation.closedAt == null

    private suspend fun existingVote(proofId: String): Vote? =
        proofRepository.observeVotesForProof(proofId, groupId)
            .first()
            .firstOrNull { it.userId == currentUserId }
}
