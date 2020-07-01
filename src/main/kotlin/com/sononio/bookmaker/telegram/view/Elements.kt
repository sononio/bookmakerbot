package com.sononio.bookmaker.telegram.view

import com.sononio.bookmaker.model.lot.Bet
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.model.lot.NumericLot
import com.sononio.bookmaker.model.lot.PercentLot

fun lotName(lot: Lot, forceResult: Boolean = false) =
        message {
            plain { text = "${lot.status} " }
            bold { text = "[${lot.getId()}] ${lot.name}." }
            required(lot.getResult()) {
                requiredTrue(forceResult || lot.status == Lot.Status.FINISH) {
                    bold { text = " ${lot.getResultRawValue()}" }
                }
            }
        }

fun lotShort(lot: Lot, forceResult: Boolean = false) =
        message {
            import { lotName(lot, forceResult) }
            required(lot.endBetsTime) {
                requiredTrue(lot.status == Lot.Status.ACTIVE) {
                    plain { text = " (до ${lot.endBetsTime})" }
                }
            }
            required(lot.resultsTime) {
                requiredTrue(lot.status == Lot.Status.RESULTS) {
                    plain { text = " (итоги ${lot.resultsTime})" }
                }
            }
            newline()
            italic { text = lot.question }
        }

fun lotList(lots: Iterable<Lot>, forceResult: Boolean = false) =
        message {
            lots.forEach {
                import { lotShort(it, forceResult) }
                newline(2)
            }
        }

fun userBet(bet: Bet) =
        message {
            import { lotName(bet.lot) }
            plain { text = " -> ${bet.getBetValueRaw()}" }
            requiredTrue(bet.lot.status == Lot.Status.FINISH) {
                plain {
                    requiredTrue(bet.isWinner) { plain { text = " (ПОБЕДА \uD83C\uDF89)" } }
                    requiredFalse(bet.isWinner) { plain { text = " (ПОРАЖЕНИЕ)" } }
                }
            }
        }

fun lotExplain(lot: Lot) =
        message {
            bold { text = "${lot.status} ${lot.name}." }
            newline()
            plain { text = "${lot.question}? (${lot.lotType()})" }
            newline()
            requiredTrue(lot is NumericLot) {
                plain { text = "Необходимая точность: ${(lot as NumericLot).allowedError}" }
                requiredTrue(lot is PercentLot) { plain { text = "%" }}
            }
            newline(2)
            plain { text = lot.description }
            newline(2)
            required(lot.endBetsTime) {
                italic { text = "Конец ставок: ${lot.endBetsTime}" }
                newline()
            }
            required(lot.resultsTime) {
                italic { text = "Результаты: ${lot.resultsTime}" }
                newline()
            }
        }

fun betShort(bet: Bet) =
        message {
            plain { text = bet.user.userName.toString() }
            plain { text = " -> ${bet.getBetValueRaw()}" }
        }