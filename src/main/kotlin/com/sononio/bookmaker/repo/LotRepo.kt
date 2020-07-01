package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.lot.Lot
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface LotRepo : KCrudRepo<Lot, Long> {
    @Transactional
    fun findTop1ByOrderByUpdateTimeDesc(): Lot?
    @Transactional
    fun findByResultsCalculatedIsFalseOrResultsTimeIsNullOrEndBetsTimeAfter(endBetsTime: Date): Iterable<Lot>
    @Transactional
    fun findByEndBetsTimeAfterOrEndBetsTimeIsNull(endBetsTime: Date): Iterable<Lot>
    fun findAllByResultsCalculatedAndResultsTimeIsBefore(resultsCalculated: Boolean, resultsTime: Date): Iterable<Lot>
    fun findAllByEndBetsTimeIsBeforeAndHasResultIsFalse(endBetsTime: Date): Iterable<Lot>
    fun countByEndBetsTimeIsNullOrEndBetsTimeAfter(endBetsTime: Date): Int
}