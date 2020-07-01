package com.sononio.bookmaker.schedule

import com.sononio.bookmaker.model.Chat
import com.sononio.bookmaker.model.Notification
import com.sononio.bookmaker.service.LotService
import com.sononio.bookmaker.service.NotificationService
import com.sononio.bookmaker.service.UserService
import com.sononio.bookmaker.telegram.view.Plain
import com.sononio.bookmaker.telegram.view.lotWithNoResultNotification
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdminNotificationSchedule(
        private val lotService: LotService,
        private val userService: UserService,
        private val notificationService: NotificationService) {
    companion object {
        val LOG by lazyLogger()
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    fun notifyAdminsAboutEndBets() {
        LOG.debug("Start notifyAdminsAboutEndBets")
        val lots = lotService.findAllWaitingResultsAndWithoutResultValueLots()
                .filter {
                    it.notifications.find { it.type == Notification.Type.END_BET_NOTIFICATION } == null
                }

        val adminsChats = userService.findAdmins().map { it.chats }.flatten().filter { it.userChat }

        val messages = mutableMapOf<Chat, Plain>()
        for (lot in lots) {
            for (adminsChat in adminsChats) {
                messages[adminsChat] = lotWithNoResultNotification(lot)
            }
        }

        userService.notifyUsers(messages, true)

        val notifications = mutableListOf<Notification>()
        for (lot in lots) {
            val notification = Notification(Notification.Type.END_BET_NOTIFICATION)
            notification.lot = lot
            notifications.add(notification)
        }

        notificationService.saveAll(notifications)
        LOG.debug("Stop notifyAdminsAboutEndBets")
    }
}