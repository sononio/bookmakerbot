package com.sononio.bookmaker.model.lot

import com.sononio.bookmaker.model.BaseEntity
import com.sononio.bookmaker.model.Notification
import com.sononio.bookmaker.model.User
import java.util.*
import javax.persistence.*

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(
        name="lots",
        uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("name"))])
@SequenceGenerator(name = "default_gen", sequenceName = "lot_seq", allocationSize = 1)
abstract class Lot(
        var name: String,
        @Lob var description: String,
        @Lob var question: String,
        var endBetsTime: Date?,
        var resultsTime: Date?,
        var resultsCalculated: Boolean = false,
        var hasResult: Boolean = false
) : BaseEntity<Long>(), Comparable<Lot> {

    @OneToMany(mappedBy = "lot", cascade = [CascadeType.ALL])
    var bets: MutableSet<Bet> = mutableSetOf()

    @OneToMany(mappedBy = "lot")
    var notifications: MutableSet<Notification> = mutableSetOf()

    abstract fun isWin(bet: Any): Boolean
    abstract fun lotType(): LotType
    abstract fun getResult(): Any?
    abstract fun getResultRawValue(): String?
    abstract fun createBet(user: User, betValue: Any): Bet

    val status: Status get() {
        return when {
            endBetsTime == null || endBetsTime!! > Date() -> Status.ACTIVE
            !resultsCalculated || resultsTime == null || resultsTime!! > Date() -> Status.RESULTS
            else -> Status.FINISH
        }
    }

    @PreUpdate
    private fun preUpdate() {
        if (resultsTime != null && endBetsTime == null) endBetsTime = resultsTime
        hasResult = getResult() != null
    }

    override fun compareTo(other: Lot): Int {
        return Comparator.comparing(Lot::status)
                .thenComparing(Lot::endBetsTime)
                .compare(this, other)
    }

    enum class LotType {
        INT,
        PERCENT;

        override fun toString(): String = when(this) {
            INT -> "Целочисленное значение"
            PERCENT -> "Процент"
        }
    }

    enum class Status : Comparable<Status> {
        ACTIVE,
        RESULTS,
        FINISH;

        override fun toString(): String = when (this) {
            ACTIVE -> "\uD83D\uDFE2"
            RESULTS -> "⚪"
            FINISH -> "⚫"
        }
    }
}