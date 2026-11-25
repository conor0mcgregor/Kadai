package com.example.kadai.data.repository

import com.example.kadai.data.mock.MockData
import com.example.kadai.domain.model.User
import com.example.kadai.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUserRepository : UserRepository {

    private val usersState = MutableStateFlow(MockData.users.toMutableList())

    override fun observeUser(uid: String): Flow<User?> =
        usersState.map { list -> list.find { it.uid == uid } }

    override suspend fun getUser(uid: String): User? =
        usersState.value.find { it.uid == uid }

    override suspend fun saveUser(user: User) {
        val current = usersState.value.toMutableList()
        val index = current.indexOfFirst { it.uid == user.uid }
        if (index >= 0) current[index] = user else current.add(user)
        usersState.value = current
    }

    override suspend fun updateStreak(uid: String, streak: Int) {
        val user = getUser(uid) ?: return
        saveUser(user.copy(streak = streak))
    }
}
