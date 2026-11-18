package com.example.kadai.domain.repository

import com.example.kadai.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeNotifications(userId: String): Flow<List<Notification>>
    suspend fun getUnreadCount(userId: String): Int
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead(userId: String)
}
