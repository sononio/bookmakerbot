package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.lot.IntLot
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.model.lot.PercentLot
import com.sononio.bookmaker.repo.LotRepo
import com.sononio.bookmaker.telegram.mapper.lot.IntLotMapper
import com.sononio.bookmaker.telegram.mapper.lot.LotMapper
import com.sononio.bookmaker.telegram.mapper.lot.LotTypeMessage
import com.sononio.bookmaker.telegram.mapper.lot.PercentLotMapper
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class LotService(
        private val lotRepo: LotRepo
) {
    companion object {
        val LOG by lazyLogger()
    }

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

    fun parseLot(str: String, basedOn: Lot? = null): Lot {
        val lines = str.trim().lines()

        val mapper = when(basedOn) {
            null -> LotTypeMessage.valueOf(lines[0].toUpperCase()).mapper()
            is IntLot -> IntLotMapper().withBasedOn(basedOn as? IntLot)
            is PercentLot -> PercentLotMapper().withBasedOn(basedOn as? PercentLot)
            else -> throw IllegalStateException("Unknown lot type: ${basedOn::class}")
        } as LotMapper<out Lot>

        val linesToMap = if (basedOn != null) lines else lines.slice(1 until lines.size)

        return mapper.mapStrings(linesToMap).map()
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