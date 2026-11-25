package com.example.kadai.domain.repository

import com.example.kadai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(uid: String): Flow<User?>
    suspend fun getUser(uid: String): User?
    suspend fun saveUser(user: User)
    suspend fun updateStreak(uid: String, streak: Int)
}
