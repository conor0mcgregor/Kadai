package com.example.kadai.domain.repository

import com.example.kadai.domain.model.Score
import kotlinx.coroutines.flow.Flow

interface RankingRepository {
    fun observeRankingForGroup(groupId: String): Flow<List<Score>>
    suspend fun getTotalPointsForUser(userId: String, groupId: String): Int
    suspend fun getScoresForChallenge(dailyChallengeId: String, groupId: String): List<Score>
}
