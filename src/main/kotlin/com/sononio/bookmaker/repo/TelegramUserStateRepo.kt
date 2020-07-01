package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.TelegramUserState
import com.sononio.bookmaker.model.User
import org.springframework.stereotype.Repository

@Repository
interface TelegramUserStateRepo : KCrudRepo<TelegramUserState, Int> {
    fun findByUser(user: User): TelegramUserState?
}