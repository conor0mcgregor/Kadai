package com.example.kadai.domain.model

data class Challenge(
    val challengeId: String,
    val title: String,
    val description: String,
    val category: String = "",
    val createdBy: String,
    val status: ChallengeStatus = ChallengeStatus.PENDING,
    val createdAt: Long = 0L
)

enum class ChallengeStatus { PENDING, APPROVED, REJECTED }
