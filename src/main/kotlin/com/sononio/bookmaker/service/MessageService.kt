package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.Message
import com.sononio.bookmaker.repo.MessageRepo
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MessageService(
        private val messageRepo: MessageRepo,
        private val userService: UserService,
        private val lotService: LotService,
        private val betService: BetService,
        private val telegramUserStateService: TelegramUserStateService
) {
    companion object { val LOG by lazyLogger() }

    @Value("\${telegram.invite_code}")
    private lateinit var inviteCode: String

    fun save(message: Message) : Message {
        return messageRepo.save(message)
    }
}