package com.example.kadai.domain.model

data class Group(
    val groupId: String,
    val name: String,
    val description: String = "",
    val photoUrl: String? = null,
    val adminId: String,
    val createdAt: Long = 0L,
    val status: GroupStatus = GroupStatus.ACTIVE,
    val emoji: String = "🏆",
    val inviteCode: String = ""
)

enum class GroupStatus { ACTIVE, ARCHIVED }
