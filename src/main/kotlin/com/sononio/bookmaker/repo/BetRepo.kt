package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.model.lot.Bet
import com.sononio.bookmaker.model.lot.Lot
import org.springframework.stereotype.Repository

@Repository
interface BetRepo : KCrudRepo<Bet, Long> {
    fun findByUserAndLot(user: User, lot: Lot): Bet?
}