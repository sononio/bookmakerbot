package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.lot.IntLot
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.model.lot.PercentLot
import com.sononio.bookmaker.repo.LotRepo
import com.sononio.bookmaker.util.enhancement.toDate
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class LotService(
        private val lotRepo: LotRepo
) {
    companion object {
        val LOG by lazyLogger()
    }

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    fun save(lot: Lot): Lot {
        return lotRepo.save(lot)
    }

    fun findById(id: Long): Lot? {
        return lotRepo.findByUid(id)
    }

    fun findLastLot(): Lot {
        return lotRepo.findTop1ByOrderByUpdateTimeDesc()!!
    }

    fun findActiveLots(): Iterable<Lot> {
        return lotRepo.findByEndBetsTimeAfterOrEndBetsTimeIsNull(Date()).sorted()
    }

    fun findNotFinishedLots(): Iterable<Lot> {
        return lotRepo.findByResultsCalculatedIsFalseOrResultsTimeIsNullOrEndBetsTimeAfter(Date()).sorted()
    }

    fun findActiveLotsCount(): Int {
        return lotRepo.countByEndBetsTimeIsNullOrEndBetsTimeAfter(Date())
    }

    fun parseLot(str: String): Lot {
        val lines = str.trim().lines()

        val lotType = Lot.LotType.valueOf(lines[0].toUpperCase())
        val name = lines[1]
        val description = lines[2]
        val question = lines[3]
        val maxError = if (lines.size < 5 || lines[4].isEmpty()) null else BigDecimal(lines[4])
        val endBetsTime = if (lines.size < 6 || lines[5].isEmpty()) null else lines[5].toDate(DATE_FORMATTER)
        val resultsTime = if (lines.size < 7 || lines[6].isEmpty()) null else lines[6].toDate(DATE_FORMATTER)

        return when (lotType) {
            Lot.LotType.INT -> IntLot(name, description, question, endBetsTime, resultsTime, maxError)
            Lot.LotType.PERCENT -> PercentLot(name, description, question, endBetsTime, resultsTime, maxError)
        }
    }

    fun updateResult(lot: Lot, value: String): Lot {
        when (lot) {
            is IntLot -> lot.resultInt = value.toInt()
            is PercentLot -> lot.resultPercent = value.toBigDecimal()
        }

        return lotRepo.save(lot)
    }

    fun calculateResults(lot: Lot): Boolean {
        if (lot.getResult() == null) return false
        if (lot.resultsTime == null || lot.resultsTime!! > Date()) lot.resultsTime = Date()

        for (bet in lot.bets) {
            bet.isWinner = bet.calculateWin()
        }
        lot.resultsCalculated = true

        lotRepo.save(lot)
        LOG.info("Lot result calculations: \"{}\". Total bets: {}. Winners: {}",
                lot.name, lot.bets.size, lot.bets.filter { it.isWinner }.size)

        return true
    }

    @Transactional
    fun finishAllExpiredLots(): Iterable<Lot> {
        val lots = lotRepo.findAllByResultsCalculatedAndResultsTimeIsBefore(false, Date())
        val finishedLots: MutableList<Lot> = mutableListOf()
        for (lot in lots) {
            if (calculateResults(lot)) {
                finishedLots.add(lot)
            }
        }
        return finishedLots
    }

    fun findAllWaitingResultsAndWithoutResultValueLots(): Iterable<Lot> {
        return lotRepo.findAllByEndBetsTimeIsBeforeAndHasResultIsFalse(Date()).sorted()
    }

    fun createLotFromAdmin(lot: Lot) {
        save(lot)
    }

    fun updateLotResult(lot: Lot, value: String) {
        updateResult(lot, value)
    }
}