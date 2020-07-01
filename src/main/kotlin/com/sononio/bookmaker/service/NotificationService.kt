package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.Notification
import com.sononio.bookmaker.repo.NotificationRepo
import org.springframework.stereotype.Service

@Service
class NotificationService(
        private val notificationRepo: NotificationRepo
) {
    fun save(notification: Notification): Notification {
        return notificationRepo.save(notification)
    }

    fun saveAll(notifications: Iterable<Notification>): Iterable<Notification> {
        return notificationRepo.saveAll(notifications)
    }
}