package com.example.kadai.domain.repository

import com.example.kadai.domain.model.GroupValidation
import com.example.kadai.domain.model.Proof
import com.example.kadai.domain.model.Reaction
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.domain.model.Vote
import kotlinx.coroutines.flow.Flow

interface ProofRepository {
    fun observeProofsForChallenge(dailyChallengeId: String): Flow<List<Proof>>
    fun observeVotesForGroup(groupId: String): Flow<List<Vote>>
    fun observeVotesForProof(proofId: String, groupId: String): Flow<List<Vote>>
    fun observeReactionsForProof(proofId: String): Flow<List<Reaction>>
    fun observeFavoriteProofIds(userId: String): Flow<Set<String>>
    suspend fun getProof(proofId: String): Proof?
    suspend fun getProofsForGroup(groupId: String, dailyChallengeId: String): List<Proof>
    suspend fun submitProof(proof: Proof)
    suspend fun castVote(vote: Vote)
    suspend fun toggleReaction(userId: String, proofId: String, type: ReactionType)
    suspend fun toggleFavorite(userId: String, proofId: String)
    suspend fun getValidationForGroup(proofId: String, groupId: String): GroupValidation?
}
