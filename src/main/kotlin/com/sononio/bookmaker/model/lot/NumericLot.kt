package com.sononio.bookmaker.model.lot

import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity

@Entity
abstract class NumericLot(
        name: String,
        description: String,
        question: String,
        endBetsTime: Date?,
        resultsTime: Date?,
        var allowedError: BigDecimal? = null
) : Lot(name, description, question, endBetsTime, resultsTime)