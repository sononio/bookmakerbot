package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.model.lot.Bet
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.repo.BetRepo
import org.springframework.stereotype.Service

@Service
class BetService(
        private val betRepo: BetRepo
) {
    fun createOrUpdateBet(user: User, lot: Lot, betValue: Any): Bet {
        val bet = betRepo.findByUserAndLot(user, lot) ?: lot.createBet(user, betValue)
        bet.updateBetValue(betValue)
        return betRepo.save(bet)
    }
}