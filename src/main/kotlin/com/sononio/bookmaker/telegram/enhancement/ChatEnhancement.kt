package com.sononio.bookmaker.telegram.enhancement

import org.telegram.telegrambots.meta.api.objects.Chat

fun Chat.name(): String = "$firstName $lastName"