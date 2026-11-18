package com.example.kadai.data.repository

import com.example.kadai.data.mock.MockData
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.Membership
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeGroupRepository : GroupRepository {

    private val groupsState = MutableStateFlow(MockData.groups.toMutableList())
    private val membershipsState = MutableStateFlow(MockData.memberships.toMutableList())

    override fun observeGroupsForUser(userId: String): Flow<List<Group>> {
        return membershipsState.map { memberships ->
            val groupIds = memberships
                .filter { it.userId == userId && it.status == MembershipStatus.ACTIVE }
                .map { it.groupId }
            groupsState.value.filter { it.groupId in groupIds }
        }
    }

    override suspend fun getGroup(groupId: String): Group? =
        groupsState.value.find { it.groupId == groupId }

    override suspend fun getMembershipsForUser(userId: String): List<Membership> =
        membershipsState.value.filter { it.userId == userId && it.status == MembershipStatus.ACTIVE }

    override suspend fun getMembershipsForGroup(groupId: String): List<Membership> =
        membershipsState.value.filter { it.groupId == groupId && it.status == MembershipStatus.ACTIVE }

    override suspend fun createGroup(group: Group, adminMembership: Membership) {
        groupsState.value = (groupsState.value + group).toMutableList()
        membershipsState.value = (membershipsState.value + adminMembership).toMutableList()
    }

    override suspend fun updateGroup(group: Group) {
        val current = groupsState.value.toMutableList()
        val index = current.indexOfFirst { it.groupId == group.groupId }
        if (index >= 0) current[index] = group
        groupsState.value = current
    }

    override suspend fun addMember(membership: Membership) {
        membershipsState.value = (membershipsState.value + membership).toMutableList()
    }

    override suspend fun removeMember(groupId: String, userId: String) {
        val current = membershipsState.value.toMutableList()
        val index = current.indexOfFirst { it.groupId == groupId && it.userId == userId }
        if (index >= 0) {
            current[index] = current[index].copy(
                status = MembershipStatus.INACTIVE,
                leftAt = System.currentTimeMillis()
            )
        }
        membershipsState.value = current
    }
}
