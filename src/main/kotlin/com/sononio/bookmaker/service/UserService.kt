package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.Chat
import com.sononio.bookmaker.model.TelegramUserState
import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.repo.UserRepo
import com.sononio.bookmaker.telegram.BookmakerAdminBot
import com.sononio.bookmaker.telegram.BookmakerBot
import com.sononio.bookmaker.telegram.Bot
import com.sononio.bookmaker.telegram.view.Plain
import com.sononio.bookmaker.telegram.view.lotNotification
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepo: UserRepo,
        private val telegramUserStateService: TelegramUserStateService,
        private val lotService: LotService,
        private val context: ApplicationContext,
        private val chatService: ChatService
) {
    @Value("\${telegram.admin}")
    private lateinit var adminUsername: String

    fun findUser(userId: Int? = null, telegramId: Int? = null): User? {
        if (userId == null && telegramId == null) return null

        return if (userId != null && telegramId == null)
            userRepo.findByUid(userId)
        else if (userId == null && telegramId != null)
            userRepo.findByTelegramId(telegramId)
        else
            userRepo.findByUidAndTelegramId(userId!!, telegramId!!)
    }

    fun findAdmins(): Iterable<User> {
        return userRepo.findByIsAdmin(true)
    }

    fun saveUser(user: User): User {
        if (user.userName == adminUsername) {
            user.isAdmin = true
        }
        return userRepo.save(user)
    }

    fun dropStateToRegistration(user: User): User {
        val userState = telegramUserStateService.save(TelegramUserState(state = TelegramUserState.State.REGISTRATION))
        telegramUserStateService.goToRegistration(userState)
        user.userState = userState
        return saveUser(user)
    }

    fun approve(user: User): User {
        user.approved = true
        return saveUser(user)
    }

    fun notifyUsers(messages: Map<Chat, Plain>, useAdminBot: Boolean = false) {
        val bot = when (useAdminBot) {
            false -> context.getBean(BookmakerBot::class.java)
            true -> context.getBean(BookmakerAdminBot::class.java)
        }

        for ((chat, mes) in messages) {
            bot.trySendMessage(Bot.messageOf(chat, mes.toString(), markdown = true))
        }

    }

    fun notifyAllUsers(messageGenerator: (User) -> Plain) {
        val chats = chatService.findAll()
        val bot = context.getBean(BookmakerBot::class.java)

        for (chat in chats) {
            bot.trySendMessage(Bot.messageOf(chat, messageGenerator(chat.users.first()).toString(), markdown = true))
        }
    }

    fun notifyAllUsersAboutLot(lot: Lot) {
        notifyAllUsers { lotNotification(lot) }
    }
}