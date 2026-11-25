package com.example.kadai.ui.screens.proof

import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.model.ReactionType

enum class ProofFilter(val label: String) {
    ALL("Todas"),
    PENDING("Pendientes"),
    VALID("Válidas"),
    REJECTED("Rechazadas")
}

data class GroupProofItem(
    val proofId: String,
    val authorName: String,
    val authorInitial: String,
    val mediaType: MediaType,
    val storagePath: String,
    val submittedAtText: String,
    val positiveVotes: Int,
    val negativeVotes: Int,
    val userVote: Boolean?,
    val isOwn: Boolean,
    val isPending: Boolean,
    val isValid: Boolean,
    val reactions: Map<ReactionType, Int>,
    val myReactions: Set<ReactionType>
)

data class GroupProofsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val groupId: String = "",
    val groupName: String = "",
    val groupPhotoUrl: String? = null,
    val challengeTitle: String = "",
    val isOpen: Boolean = false,
    val filter: ProofFilter = ProofFilter.ALL,
    val items: List<GroupProofItem> = emptyList(),
    val memberCount: Int = 0
)
