package com.sononio.bookmaker.model.lot

import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.telegram.TelegramBetValueParseException
import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import kotlin.math.absoluteValue

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class IntLot(
        name: String,
        description: String,
        question: String,
        endBetsTime: Date? = null,
        resultsTime: Date? = null,
        allowedError: BigDecimal? = null,
        var resultInt: Int? = null
) : NumericLot(name, description, question, endBetsTime, resultsTime, allowedError) {
    override fun isWin(bet: Any): Boolean {
        if (bet !is Int || resultInt == null || allowedError == null) return false
        return (bet - resultInt!!).absoluteValue <= allowedError!!.toInt()
    }

    override fun lotType(): LotType = LotType.INT

    override fun getResult() = resultInt
    override fun getResultRawValue() = resultInt?.toString()
    override fun createBet(user: User, betValue: Any): Bet {
        val bet = IntBet(IntBet.convertToInt(betValue)!!)
        bet.user = user
        bet.lot = this

        return bet
    }

}