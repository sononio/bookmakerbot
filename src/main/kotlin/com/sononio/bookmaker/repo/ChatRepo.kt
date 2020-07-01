package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.Chat
import org.springframework.stereotype.Repository

@Repository
interface ChatRepo : KCrudRepo<Chat, Int> {
    fun findByTelegramId(telegramId: Long) : Chat?
    fun findByUidAndTelegramId(userId: Int, telegramId: Long) : Chat?
}