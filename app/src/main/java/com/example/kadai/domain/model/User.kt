package com.example.kadai.domain.model

data class User(
    val uid: String,
    val username: String,
    val email: String,
    val photoUrl: String? = null,
    val createdAt: Long = 0L,
    val streak: Int = 0
)
