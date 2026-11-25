package com.example.kadai.ui.screens.proof

import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.model.ReactionType

data class ProofDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val proofId: String = "",
    val groupId: String = "",
    val groupName: String = "",
    val challengeTitle: String = "",
    val authorName: String = "",
    val authorInitial: String = "?",
    val mediaType: MediaType = MediaType.PHOTO,
    val storagePath: String = "",
    val submittedAtText: String = "",
    val isOwn: Boolean = false,
    val isOpen: Boolean = false,
    val positiveVotes: Int = 0,
    val negativeVotes: Int = 0,
    val userVote: Boolean? = null,
    val isPending: Boolean = true,
    val isValid: Boolean = false,
    val reactions: Map<ReactionType, Int> = emptyMap(),
    val myReactions: Set<ReactionType> = emptySet(),
    val isFavorite: Boolean = false
)
