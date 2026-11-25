package com.example.kadai.domain.model

data class Notification(
    val notificationId: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val relatedId: String? = null,
    val createdAt: Long,
    val readAt: Long? = null
)

enum class NotificationType {
    NEW_CHALLENGE,
    PROOF_VOTED,
    RANKING_UPDATED,
    CHALLENGE_CLOSED,
    REACTION_RECEIVED,
    GROUP_INVITATION
}
