package com.example.kadai.domain.model

data class Score(
    val groupId: String,
    val userId: String,
    val dailyChallengeId: String,
    val completionPoints: Int = 0,
    val positionPoints: Int = 0,
    val timePoints: Int = 0,
    val totalPoints: Int = 0,
    val calculatedAt: Long = 0L
)
