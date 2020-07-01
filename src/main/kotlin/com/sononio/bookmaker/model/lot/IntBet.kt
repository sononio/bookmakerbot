package com.sononio.bookmaker.model.lot

import com.sononio.bookmaker.telegram.TelegramBetValueParseException
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class IntBet(
        betValue: Int
) : NumericBet(betValue.toBigDecimal()) {
    companion object {
        fun convertToInt(betValue: Any) = when (betValue) {
            is Int -> betValue
            is BigDecimal -> {
                if (betValue.stripTrailingZeros().scale() <= 0) betValue.intValueExact()
                else throw TelegramBetValueParseException(betValue.toString()) }
            is String -> betValue.toIntOrNull().also { if (it == null) throw TelegramBetValueParseException(betValue) }
            else -> throw TelegramBetValueParseException(betValue::class.toString())
        }
    }

    override fun getBetValueObject() = betValue
    override fun getBetValueRaw() = betValue.toString()
    override fun updateBetValue(betValue: Any) {
        this.betValue = convertToInt(betValue)!!.toBigDecimal()
    }

    override fun calculateWin(): Boolean {
        val intLot = lot as IntLot
        if (intLot.allowedError == null || intLot.resultInt == null) return false

        return (betValue - intLot.resultInt!!.toBigDecimal()).abs() <= intLot.allowedError!!
    }
}