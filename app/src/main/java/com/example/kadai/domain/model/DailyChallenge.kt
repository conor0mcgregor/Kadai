package com.example.kadai.domain.model

data class DailyChallenge(
    val dailyChallengeId: String,
    val challengeId: String,
    val publishedAt: Long,
    val closesAt: Long,
    val status: DailyChallengeStatus = DailyChallengeStatus.OPEN
)

enum class DailyChallengeStatus { OPEN, CLOSED }
