package com.example.kadai.data.repository

import com.example.kadai.data.mock.MockData
import com.example.kadai.domain.model.Challenge
import com.example.kadai.domain.model.DailyChallenge
import com.example.kadai.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeChallengeRepository : ChallengeRepository {

    private val activeChallengeState = MutableStateFlow<DailyChallenge?>(MockData.activeChallenge)
    private val challengesState = MutableStateFlow(MockData.challenges.toMutableList())

    override fun observeActiveChallenge(): Flow<DailyChallenge?> =
        activeChallengeState.asStateFlow()

    override suspend fun getActiveChallenge(): DailyChallenge? =
        activeChallengeState.value

    override suspend fun getChallenge(challengeId: String): Challenge? =
        challengesState.value.find { it.challengeId == challengeId }

    override suspend fun getChallengeHistory(): List<DailyChallenge> =
        MockData.challengeHistory

    override suspend fun proposeChallenge(challenge: Challenge) {
        challengesState.value = (challengesState.value + challenge).toMutableList()
    }
}
