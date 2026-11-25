package com.example.kadai.domain.model

data class Vote(
    val voteId: String,
    val proofId: String,
    val groupId: String,
    val userId: String,
    val isPositive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
