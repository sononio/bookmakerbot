package com.sononio.bookmaker.model.mapper

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User

fun Chat.map(chat: com.sononio.bookmaker.model.Chat? = null) : com.sononio.bookmaker.model.Chat {
    return if (chat == null) {
        com.sononio.bookmaker.model.Chat(this.id, this.isUserChat, this.title, this.userName, this.firstName, this.lastName)
    } else {
        chat.telegramId = id
        chat.userChat = isUserChat
        chat.title = title
        chat.userName = userName
        chat.firstName = firstName
        chat.lastName = lastName
        chat
    }
}

fun User.map(user: com.sononio.bookmaker.model.User?): com.sononio.bookmaker.model.User {
    return if (user == null) {
        com.sononio.bookmaker.model.User(id, bot, firstName, lastName, userName, languageCode)
    } else {
        user.telegramId = id
        user.bot = bot
        user.firstName = firstName
        user.lastName = lastName
        user.userName = userName
        user.languageCode = languageCode
        return user
    }
}

fun Message.map(message: com.sononio.bookmaker.model.Message? = null): com.sononio.bookmaker.model.Message {
    return if (message == null) {
        com.sononio.bookmaker.model.Message(text)
    } else {
        message.text = text
        return message
    }
}