package com.sononio.bookmaker.telegram

import java.lang.Exception

open class TelegramException(e: Exception) : Exception(e)

class TelegramFindEntityException(e: Exception) : TelegramException(e)
class TelegramLotIsNotActiveException : TelegramException(Exception("Lot is not in active status"))
class TelegramBetValueParseException(wrongValue: String) : TelegramException(Exception("Cant parse value $wrongValue"))