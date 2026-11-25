package com.example.kadai.data.repository

import com.example.kadai.data.mock.MockData
import com.example.kadai.domain.model.Notification
import com.example.kadai.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNotificationRepository : NotificationRepository {

    private val notificationsState = MutableStateFlow(MockData.notifications.toMutableList())

    override fun observeNotifications(userId: String): Flow<List<Notification>> =
        notificationsState.map { list ->
            list.filter { it.userId == userId }.sortedByDescending { it.createdAt }
        }

    override suspend fun getUnreadCount(userId: String): Int =
        notificationsState.value.count { it.userId == userId && it.readAt == null }

    override suspend fun markAsRead(notificationId: String) {
        val current = notificationsState.value.toMutableList()
        val index = current.indexOfFirst { it.notificationId == notificationId }
        if (index >= 0) {
            current[index] = current[index].copy(readAt = System.currentTimeMillis())
        }
        notificationsState.value = current
    }

    override suspend fun markAllAsRead(userId: String) {
        val now = System.currentTimeMillis()
        notificationsState.value = notificationsState.value.map { notif ->
            if (notif.userId == userId && notif.readAt == null) notif.copy(readAt = now)
            else notif
        }.toMutableList()
    }
}
