package com.example.kadai.domain.repository

import com.example.kadai.domain.model.Challenge
import com.example.kadai.domain.model.DailyChallenge
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun observeActiveChallenge(): Flow<DailyChallenge?>
    suspend fun getActiveChallenge(): DailyChallenge?
    suspend fun getChallenge(challengeId: String): Challenge?
    suspend fun getChallengeHistory(): List<DailyChallenge>
}
