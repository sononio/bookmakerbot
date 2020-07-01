package com.sononio.bookmaker.telegram

import com.sononio.bookmaker.model.Message
import com.sononio.bookmaker.service.ChatService
import com.sononio.bookmaker.service.MessageService
import com.sononio.bookmaker.service.TelegramUserStateService
import com.sononio.bookmaker.service.UserService
import com.sononio.bookmaker.util.StateMachine
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Component
class BookmakerBot(
        chatService: ChatService,
        messageService: MessageService,

        private val userService: UserService,
        private val telegramUserStateService: TelegramUserStateService,
        private val stateMachine: StateMachine
) : Bot(userService, chatService, messageService) {

    @Value("\${telegram.bot-token}")
    private lateinit var botToken: String

    override fun getBotUsername(): String = "Bookmaker Bot"
    override fun getBotToken(): String = botToken

    override fun proceedMessage(message: Message): SendMessage? {
        LOG.info("Message from {}: {}", message.chat.name, message.text)

        if (telegramUserStateService.findByUser(message.user!!) == null) {
            userService.dropStateToRegistration(message.user!!)
        }

        stateMachine.proceedMessage(message)
        return messageOf(text = stateMachine.genMessage(message.user!!), markdown = true)
    }
}