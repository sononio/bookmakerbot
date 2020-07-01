package com.sononio.bookmaker.model.lot

import com.sononio.bookmaker.telegram.TelegramBetValueParseException
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class PercentBet(
        betValue: BigDecimal
) : NumericBet(betValue) {
    companion object {
        fun convertToPercent(betValue: Any) = when (betValue) {
            is BigDecimal -> betValue
            is String -> betValue.toBigDecimalOrNull()
                    .also { if (it == null) throw TelegramBetValueParseException(betValue) }

            else -> throw TelegramBetValueParseException(betValue::class.toString())
        }
    }
    override fun getBetValueObject() = betValue
    override fun getBetValueRaw() = "$betValue%"
    override fun updateBetValue(betValue: Any) {
        this.betValue = convertToPercent(betValue)!!
    }

    override fun calculateWin(): Boolean {
        val percentLot = lot as PercentLot
        if (percentLot.allowedError == null || percentLot.resultPercent == null) return false

        return (betValue - percentLot.resultPercent!!).abs() <= percentLot.allowedError!!
    }
}