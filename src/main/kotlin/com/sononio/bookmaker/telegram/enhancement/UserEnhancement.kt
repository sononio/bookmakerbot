package com.sononio.bookmaker.telegram.enhancement

import org.telegram.telegrambots.meta.api.objects.User

fun User.name(): String = "$firstName $lastName"