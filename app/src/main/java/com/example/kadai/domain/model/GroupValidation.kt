package com.example.kadai.domain.model

data class GroupValidation(
    val proofId: String,
    val groupId: String,
    val status: ValidationStatus = ValidationStatus.PENDING,
    val positiveVotes: Int = 0,
    val negativeVotes: Int = 0,
    val isValid: Boolean = false,
    val closedAt: Long? = null
)

enum class ValidationStatus { PENDING, CLOSED }
