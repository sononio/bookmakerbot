package com.sononio.bookmaker.model

import com.sononio.bookmaker.model.lot.Bet
import javax.persistence.*

@Entity
@Table(name = "users")
@SequenceGenerator(name = "default_gen", sequenceName = "user_seq", allocationSize = 1)
class User(
        var telegramId: Int,
        var bot: Boolean,
        var firstName: String,
        var lastName: String? = null,
        var userName: String? = null,
        var languageCode: String? = null,
        var isAdmin: Boolean = false,
        var approved: Boolean = false
) : BaseEntity<Long>() {
    @OneToMany(mappedBy = "user")
    val messages: MutableSet<Message> = mutableSetOf()

    @OneToMany(mappedBy = "user")
    var bets: MutableSet<Bet> = mutableSetOf()

    @ManyToMany
    @JoinTable(name = "chat_user")
    val chats: MutableSet<Chat> = mutableSetOf()

    @OneToOne
    @JoinColumn(name = "telegram_user_state_id")
    lateinit var userState: TelegramUserState

    val name get(): String {
        var s = firstName
        lastName?.let { s += " $lastName" }
        userName?.let { s+= " (@$userName)" }
        return s
    }

    val new get() = getId() == null
}