package com.example.kadai.data.repository

import com.example.kadai.data.mock.MockData
import com.example.kadai.domain.model.GroupValidation
import com.example.kadai.domain.model.Proof
import com.example.kadai.domain.model.Vote
import com.example.kadai.domain.repository.ProofRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProofRepository : ProofRepository {

    private val proofsState = MutableStateFlow(MockData.proofs.toMutableList())
    private val votesState = MutableStateFlow(MockData.votes.toMutableList())
    private val validationsState = MutableStateFlow(MockData.groupValidations.toMutableList())

    override fun observeProofsForChallenge(dailyChallengeId: String): Flow<List<Proof>> =
        proofsState.map { list -> list.filter { it.dailyChallengeId == dailyChallengeId } }

    override suspend fun getProof(proofId: String): Proof? =
        proofsState.value.find { it.proofId == proofId }

    override suspend fun getProofsForGroup(groupId: String, dailyChallengeId: String): List<Proof> {
        val validationProofIds = validationsState.value
            .filter { it.groupId == groupId }
            .map { it.proofId }
        return proofsState.value.filter {
            it.dailyChallengeId == dailyChallengeId && it.proofId in validationProofIds
        }
    }

    override suspend fun submitProof(proof: Proof) {
        val current = proofsState.value.toMutableList()
        val index = current.indexOfFirst { it.proofId == proof.proofId }
        if (index >= 0) current[index] = proof else current.add(proof)
        proofsState.value = current
    }

    override suspend fun castVote(vote: Vote) {
        val current = votesState.value.toMutableList()
        val index = current.indexOfFirst {
            it.proofId == vote.proofId && it.groupId == vote.groupId && it.userId == vote.userId
        }
        if (index >= 0) current[index] = vote else current.add(vote)
        votesState.value = current
    }

    override suspend fun getValidationForGroup(proofId: String, groupId: String): GroupValidation? =
        validationsState.value.find { it.proofId == proofId && it.groupId == groupId }
}
