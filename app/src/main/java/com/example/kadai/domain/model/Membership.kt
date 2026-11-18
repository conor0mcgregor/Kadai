package com.example.kadai.domain.model

data class Membership(
    val groupId: String,
    val userId: String,
    val role: MemberRole = MemberRole.MEMBER,
    val status: MembershipStatus = MembershipStatus.ACTIVE,
    val joinedAt: Long = 0L,
    val leftAt: Long? = null
)

enum class MemberRole { ADMIN, MEMBER }
enum class MembershipStatus { ACTIVE, INACTIVE }
