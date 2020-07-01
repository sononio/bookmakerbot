package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.Chat
import com.sononio.bookmaker.repo.ChatRepo
import org.springframework.stereotype.Service

@Service
class ChatService(private val chatRepo: ChatRepo) {
    fun findChat(chatId: Int? = null, telegramId: Long? = null) : Chat? {
        if (chatId == null && telegramId == null) return null

        return if (chatId != null && telegramId == null)
            chatRepo.findByUid(chatId)
        else if (chatId == null && telegramId != null)
            chatRepo.findByTelegramId(telegramId)
        else
            chatRepo.findByUidAndTelegramId(chatId!!, telegramId!!)
    }

    fun saveChat(chat: Chat) : Chat {
        return chatRepo.save(chat)
    }

    fun findAll(): Iterable<Chat> {
        return chatRepo.findAll()
    }
}