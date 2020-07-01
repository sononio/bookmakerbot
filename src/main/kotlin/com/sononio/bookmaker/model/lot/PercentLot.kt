package com.sononio.bookmaker.model.lot

import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.telegram.TelegramBetValueParseException
import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class PercentLot(
        name: String,
        description: String,
        question: String,
        endBetsTime: Date? = null,
        resultsTime: Date? = null,
        allowedError: BigDecimal? = null,
        var resultPercent: BigDecimal? = null
) : NumericLot(name, description, question, endBetsTime, resultsTime, allowedError) {
    override fun isWin(bet: Any): Boolean {
        if (bet !is BigDecimal || resultPercent == null || allowedError == null) return false
        return (bet - resultPercent!!).abs() <= allowedError!!
    }

    override fun lotType(): LotType = LotType.PERCENT

    override fun getResult() = resultPercent
    override fun getResultRawValue() = resultPercent?.toString().plus("%")
    override fun createBet(user: User, betValue: Any): Bet {
        val bet = PercentBet(PercentBet.convertToPercent(betValue)!!)
        bet.user = user
        bet.lot = this

        return bet
    }
}