package com.sononio.bookmaker.util.enhancement

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun String.toDate(formatter: DateTimeFormatter): Date? {
    return LocalDateTime.parse(this, formatter).toDate()
}

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}