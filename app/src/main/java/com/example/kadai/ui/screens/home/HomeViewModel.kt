package com.example.kadai.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.domain.model.DailyChallenge
import com.example.kadai.domain.model.DailyChallengeStatus
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.Membership
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.model.Notification
import com.example.kadai.domain.model.Proof
import com.example.kadai.domain.usecase.buildGroupRanking
import com.example.kadai.domain.usecase.formatRelativeTime
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : KadaiViewModel(application) {

    private val userRepository = container.userRepository
    private val groupRepository = container.groupRepository
    private val challengeRepository = container.challengeRepository
    private val proofRepository = container.proofRepository
    private val rankingRepository = container.rankingRepository
    private val notificationRepository = container.notificationRepository

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, error = null) }
        load()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun load() {
        loadJob?.cancel()
        val proofsFlow = challengeRepository.observeActiveChallenge().flatMapLatest { daily ->
            if (daily == null) flowOf(emptyList())
            else proofRepository.observeProofsForChallenge(daily.dailyChallengeId)
        }

        loadJob = viewModelScope.launch {
            try {
                combine(
                    groupRepository.observeGroupsForUser(currentUserId),
                    challengeRepository.observeActiveChallenge(),
                    proofsFlow,
                    notificationRepository.observeNotifications(currentUserId)
                ) { groups, daily, proofs, notifications ->
                    Snapshot(groups, daily, proofs, notifications)
                }.collect { snapshot ->
                    runCatching { buildState(snapshot) }
                        .onSuccess { _state.value = it }
                        .onFailure { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: "No se pudieron cargar los datos."
                                )
                            }
                        }
                }
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudieron cargar los datos."
                    )
                }
            }
        }
    }

    private suspend fun buildState(snapshot: Snapshot): HomeUiState {
        val groups = snapshot.groups
        val memberships = groups.associate { group ->
            group.groupId to groupRepository.getMembershipsForGroup(group.groupId)
        }
        val groupStats = groups.map { group ->
            buildGroupStat(group, memberships[group.groupId].orEmpty())
        }
        val primaryGroup = groupStats.firstOrNull()
        val now = System.currentTimeMillis()
        val activeChallenge = snapshot.dailyChallenge
        val challengeContent = activeChallenge?.let { challengeRepository.getChallenge(it.challengeId) }
        val participants = primaryGroup?.let { stat ->
            snapshot.proofs.count { it.userId in stat.memberIds }
        } ?: 0
        val activity = buildActivity(snapshot, memberships, groups, now)

        return HomeUiState(
            isLoading = false,
            error = null,
            groups = groupStats.map { stat ->
                HomeGroupItem(
                    id = stat.group.groupId,
                    name = stat.group.name,
                    photoUrl = stat.group.photoUrl,
                    description = stat.group.description,
                    memberCount = stat.memberCount,
                    topRanking = stat.topRanking
                )
            },
            challenge = activeChallenge?.let { daily ->
                HomeChallengeItem(
                    dailyChallengeId = daily.dailyChallengeId,
                    title = challengeContent?.title ?: "Reto del día",
                    description = challengeContent?.description.orEmpty(),
                    category = challengeContent?.category.orEmpty(),
                    closesAt = daily.closesAt,
                    participantsText = if (primaryGroup == null) {
                        "Únete a un grupo para participar"
                    } else {
                        "$participants de ${primaryGroup.memberCount} han participado"
                    },
                    alreadySubmitted = snapshot.proofs.any { it.userId == currentUserId },
                    isOpen = daily.status == DailyChallengeStatus.OPEN && daily.closesAt > now
                )
            },
            activity = activity,
            unreadNotifications = snapshot.notifications.count { it.readAt == null }
        )
    }

    private suspend fun buildGroupStat(group: Group, memberships: List<Membership>): GroupStat {
        val memberIds = memberships.map { it.userId }
        val activeMemberCount = memberships.count { it.status == MembershipStatus.ACTIVE }
        val pointsByUser = memberIds.associateWith { userId ->
            rankingRepository.getTotalPointsForUser(userId, group.groupId)
        }
        val namesByUser = memberIds.associateWith { userId ->
            userRepository.getUser(userId)?.username ?: "Miembro"
        }
        val orderedIds = memberIds.sortedBy { namesByUser[it].orEmpty().lowercase() }
        val topRanking = buildGroupRanking(orderedIds, pointsByUser)
            .take(3)
            .map { position ->
                val name = namesByUser[position.userId] ?: "Miembro"
                HomeRankingItem(
                    userId = position.userId,
                    displayName = name,
                    initial = name.firstOrNull()?.uppercase() ?: "?",
                    points = position.points
                )
            }

        return GroupStat(
            group = group,
            memberIds = memberIds,
            memberCount = activeMemberCount,
            topRanking = topRanking
        )
    }

    private suspend fun buildActivity(
        snapshot: Snapshot,
        memberships: Map<String, List<Membership>>,
        groups: List<Group>,
        now: Long
    ): List<HomeActivityItem> {
        val groupNameById = groups.associate { it.groupId to it.name }
        val groupIdByMember = buildMap {
            memberships.forEach { (groupId, groupMembers) ->
                groupMembers.forEach { member ->
                    if (member.userId !in keys) put(member.userId, groupId)
                }
            }
        }
        val visibleAuthorIds = memberships.values.flatten().map { it.userId }.toSet()
        return snapshot.proofs
            .filter { it.userId != currentUserId && it.userId in visibleAuthorIds }
            .sortedByDescending { it.updatedAt }
            .take(6)
            .map { proof ->
                val author = userRepository.getUser(proof.userId)
                val groupName = groupIdByMember[proof.userId]
                    ?.let { groupId -> groupNameById[groupId] }
                    .orEmpty()
                HomeActivityItem(
                    id = proof.proofId,
                    userName = author?.username ?: "Miembro del grupo",
                    userInitial = author?.username?.firstOrNull()?.uppercase() ?: "?",
                    action = "Subió su prueba diaria",
                    groupName = groupName,
                    timestamp = formatRelativeTime(proof.updatedAt, now),
                    mediaType = proof.mediaType
                )
            }
            .toList()
    }

    private data class Snapshot(
        val groups: List<Group>,
        val dailyChallenge: DailyChallenge?,
        val proofs: List<Proof>,
        val notifications: List<Notification>
    )

    private data class GroupStat(
        val group: Group,
        val memberIds: List<String>,
        val memberCount: Int,
        val topRanking: List<HomeRankingItem>
    )
}
