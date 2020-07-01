package com.sononio.bookmaker.telegram

import com.sononio.bookmaker.model.Chat
import com.sononio.bookmaker.model.Message
import com.sononio.bookmaker.model.mapper.map
import com.sononio.bookmaker.service.ChatService
import com.sononio.bookmaker.service.MessageService
import com.sononio.bookmaker.service.UserService
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


abstract class Bot(private val userService: UserService,
                   private val chatService: ChatService,
                   private val messageService: MessageService) : TelegramLongPollingBot() {

    companion object {
        val LOG by lazyLogger()

        fun messageOf(chat: Chat? = null, text: String, markdown: Boolean = false): SendMessage {
            val sendMessageRequest = SendMessage()
            sendMessageRequest.chatId = chat?.telegramId.toString()
            sendMessageRequest.text = text
            sendMessageRequest.enableMarkdownV2(markdown)
            return sendMessageRequest
        }
    }

    abstract fun proceedMessage(message: Message): SendMessage?

    @Transactional
    override fun onUpdateReceived(update: Update?) {
        if (update == null || update.message.text == null) return
        val message = updateToModel(update)
        val answer = proceedMessage(message)

        if (answer != null) {
            answer.chatId = update.message.chat.id.toString()
            message.answer = answer.text
            messageService.save(message)

            trySendMessage(answer)
        }
    }

    fun trySendMessage(sendMessageRequest: SendMessage) {
        try {
            execute(sendMessageRequest)
        } catch (e: TelegramApiException) {
            LOG.warn("Error while send message to telegram chat: {}", sendMessageRequest.chatId, e)
        }
    }

    private fun updateToModel(update: Update) : Message {
        val userOpt = userService.findUser(telegramId = update.message.from.id)
        var user = update.message.from.map(userOpt)

        val chatOpt = chatService.findChat(telegramId = update.message.chat.id)
        var chat = update.message.chat.map(chatOpt)

        chat.users.add(user)
        user.chats.add(chat)
        chat = chatService.saveChat(chat)
        user = userService.saveUser(user)

        var message = update.message.map()
        message.chat = chat
        message.user = user
        message = messageService.save(message)

        return message
    }
}