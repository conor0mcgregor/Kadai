package com.example.kadai.domain.repository

import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.Membership
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun observeGroupsForUser(userId: String): Flow<List<Group>>
    suspend fun getGroup(groupId: String): Group?
    suspend fun getMembershipsForUser(userId: String): List<Membership>
    suspend fun getMembershipsForGroup(groupId: String): List<Membership>
    suspend fun createGroup(group: Group, adminMembership: Membership)
    suspend fun updateGroup(group: Group)
    suspend fun addMember(membership: Membership)
    suspend fun removeMember(groupId: String, userId: String)
}
