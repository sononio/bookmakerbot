package com.sononio.bookmaker.model.lot

import java.math.BigDecimal
import javax.persistence.Entity

@Entity
abstract class NumericBet(
        var betValue: BigDecimal
) : Bet()