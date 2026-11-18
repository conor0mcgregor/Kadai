package com.example.kadai.domain.model

data class Proof(
    val proofId: String,
    val dailyChallengeId: String,
    val userId: String,
    val mediaType: MediaType,
    val storagePath: String,
    val submittedAt: Long,
    val updatedAt: Long
)

enum class MediaType { PHOTO, VIDEO }
