package com.example.kadai.domain.repository

import com.example.kadai.domain.model.GroupValidation
import com.example.kadai.domain.model.Proof
import com.example.kadai.domain.model.Vote
import kotlinx.coroutines.flow.Flow

interface ProofRepository {
    fun observeProofsForChallenge(dailyChallengeId: String): Flow<List<Proof>>
    suspend fun getProof(proofId: String): Proof?
    suspend fun getProofsForGroup(groupId: String, dailyChallengeId: String): List<Proof>
    suspend fun submitProof(proof: Proof)
    suspend fun castVote(vote: Vote)
    suspend fun getValidationForGroup(proofId: String, groupId: String): GroupValidation?
}
