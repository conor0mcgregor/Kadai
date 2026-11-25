package com.example.kadai.data.repository

import com.example.kadai.data.mock.MockData
import com.example.kadai.domain.model.Score
import com.example.kadai.domain.repository.RankingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRankingRepository : RankingRepository {

    private val scoresState = MutableStateFlow(MockData.scores.toMutableList())

    override fun observeRankingForGroup(groupId: String): Flow<List<Score>> =
        scoresState.map { list ->
            list.filter { it.groupId == groupId }
                .groupBy { it.userId }
                .map { (userId, scores) ->
                    Score(
                        groupId = groupId,
                        userId = userId,
                        dailyChallengeId = "",
                        totalPoints = scores.sumOf { it.totalPoints },
                        calculatedAt = scores.maxOf { it.calculatedAt }
                    )
                }
                .sortedByDescending { it.totalPoints }
        }

    override suspend fun getTotalPointsForUser(userId: String, groupId: String): Int =
        scoresState.value
            .filter { it.userId == userId && it.groupId == groupId }
            .sumOf { it.totalPoints }

    override suspend fun getScoresForChallenge(dailyChallengeId: String, groupId: String): List<Score> =
        scoresState.value.filter {
            it.dailyChallengeId == dailyChallengeId && it.groupId == groupId
        }
}
