package com.sononio.bookmaker.model

import javax.persistence.*

@Entity
@Table(name = "chats")
@SequenceGenerator(name = "default_gen", sequenceName = "chat_seq", allocationSize = 1)
class Chat(
        var telegramId: Long,
        var userChat: Boolean,
        var title: String? = null,
        var userName: String? = null,
        var firstName: String? = null,
        var lastName: String? = null
) : BaseEntity<Long>() {

    @OneToMany(mappedBy = "chat")
    val messages: MutableSet<Message> = mutableSetOf()

    @ManyToMany(mappedBy = "chats")
    val users: MutableSet<User> = mutableSetOf()

    val name get(): String {
        var s = firstName ?: ""
        lastName?.let { s += " $lastName" }
        userName?.let { s+= " (@$userName)" }
        return s
    }
}