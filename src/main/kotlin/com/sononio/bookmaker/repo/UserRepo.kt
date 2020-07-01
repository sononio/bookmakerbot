package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.User
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : KCrudRepo<User, Int> {
    fun findByTelegramId(telegramId: Int) : User?
    fun findByUidAndTelegramId(userId: Int, telegramId: Int) : User?
    fun findByIsAdmin(isAdmin: Boolean): Iterable<User>
}