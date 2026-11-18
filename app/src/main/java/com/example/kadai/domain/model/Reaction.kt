package com.example.kadai.domain.model

data class Reaction(
    val reactionId: String,
    val proofId: String,
    val userId: String,
    val type: ReactionType,
    val createdAt: Long
)

enum class ReactionType { FIRE, CLAP, ROCKET, HEART, LAUGH }
