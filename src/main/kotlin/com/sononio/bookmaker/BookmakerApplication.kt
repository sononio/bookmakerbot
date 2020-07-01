package com.sononio.bookmaker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.telegram.telegrambots.ApiContextInitializer

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
class BookmakerApplication

fun main(args: Array<String>) {
    ApiContextInitializer.init()
    runApplication<BookmakerApplication>(*args)
}
