package com.sononio.bookmaker.telegram.view

import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.model.lot.Bet
import com.sononio.bookmaker.model.lot.IntLot
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.model.lot.PercentLot

val registration =
        message {
            text = "Прошу предъявить доказательство членства таверны..."
        }

fun menu(activeLotsCount: Int) =
        message {
            text = "Добро пожаловать в мою букмекерскую контору! Количество активных лотов на сегодня: $activeLotsCount"
        }

fun lotListShowUser(lots: Iterable<Lot>) =
        message {
            plain { text = "Если вас интересует какой-то конкретный лот, то укажите его номер. Либо вернитесь в /menu..." }
            newline(2)
            import { lotList(lots) }
        }

fun makeBetId(lots: Iterable<Lot>) =
        message {
            plain { text = "На какой лот будете ставить?.." }
            newline(2)
            import { lotList(lots) }
        }

fun makeBetValue(lot: Lot) =
        message {
            plain { text = "Ознакомьтесь с лотом и введите свою ставку в формате " }
            requiredTrue(lot is IntLot) { plain { text = "\"123\"" } }
            requiredTrue(lot is PercentLot) { plain { text = "\"12.34\"" } }
            plain { text = "..." }

            newline(2)
            import { lotExplainCommon(lot) }
        }

fun userBets(user: User) =
        message {
            plain { text = "Ваши последние ставки:" }
            newline(2)
            user.bets
                    .sortedWith(compareBy( {it.lot.status}, {it.updateTime} ))
                    .take(15)
                    .forEach {
                        import { userBet(it) }
                        newline()
                    }
        }

fun lotExplainCommon(lot: Lot) =
        message {
            import { lotExplain(lot) }
            requiredTrue(lot.status == Lot.Status.FINISH) { required(lot.getResult()) {
                    bold { text = "Итог: ${lot.getResultRawValue()}" }
                    newline()
                }
            } }

fun lotNotification(lot: Lot) =
        message {
            plain { text = "Взгляните на наш замечательный лот! Уверены, интуиция и рассчет Вас не подведут!" }
            newline(2)
            import { lotShort(lot) }
        }

fun resultNotification(lot: Lot, bet: Bet?) =
        message {
            plain { text = "Появились результаты по лоту \"${lot.name}\"!" }
            newline()
            required(bet) {
                import { userBet(bet!!) }
            }
            requiredNull(bet) {
                import { lotName(lot) }
            }
        }

fun lotBetsList(lot: Lot) =
        message {
            plain { text = "Все ставки по лоту" }
            newline()
            import { lotName(lot) }
            newline(2)
            lot.bets.forEach {
                import { betShort(it) }
                newline()
            }
        }