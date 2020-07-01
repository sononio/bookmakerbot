package com.sononio.bookmaker.model.lot

import com.sononio.bookmaker.model.BaseEntity
import com.sononio.bookmaker.model.User
import javax.persistence.*

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(
        name="bets",
        uniqueConstraints = [UniqueConstraint(name="user_lot_unique", columnNames=["lot_id", "user_id"])])
@SequenceGenerator(name = "default_gen", sequenceName = "bet_gen", allocationSize = 1)
abstract class Bet(var isWinner: Boolean = false) : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "lot_id")
    lateinit var lot: Lot

    @ManyToOne
    @JoinColumn(name = "user_id")
    lateinit var user: User

    abstract fun getBetValueObject() : Any
    abstract fun getBetValueRaw(): String
    abstract fun updateBetValue(betValue: Any)
    abstract fun calculateWin(): Boolean
}