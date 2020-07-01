package com.sononio.bookmaker.telegram.view

import com.sononio.bookmaker.model.lot.Lot

val adminMenuMessage = message { text = "Жду ваших приказаний, милорд..." }

val startLotMessage = message { text = """Прошу, опишите вашему слуге на что смертные будут ставить, милорд:

            |type
            |name
            |description
            |question
            |[max_error]
            |[bets_end]
            |[results]
            
            |type: INT or PERCENT
            |name: text string
            |description: text string
            |question: text string
            |max_error: number (integer or real)
            |bets_end: date in format '25-01-2012T10:40' in MSK
            |results: date in format '25-01-2012T10:40' in MSK
            
            |Оставьте строку пустой, если хотите пропустить значение, которое необязательно знать смертным, милорд.
            """.trimMargin("|")
}

val lotNotFound = message { text = "Приношу свои глубочайшие извинения, милорд! Ваш верный слуга не смог отыскать лот! Прошу, укажите на него еще раз..." }

fun lotExplainAdmin(lot: Lot) =
        message {
            import { lotExplain(lot) }
            required(lot.getResult()) {
                bold { text = "Итог: ${lot.getResultRawValue()}" }
                newline()
            }
        }

fun lotListShow(lots: Iterable<Lot>) =
        message {
            plain { text = "Укажите, какой лот мне описать более подробно, милопд..." }
            newline(2)
            import { lotList(lots) }
        }

fun lotListResult(lots: Iterable<Lot>) =
        message {
            plain { text = "Укажите, по какому лоту Вы готовы подвести итоги, милорд..." }
            newline(2)
            import { lotList(lots) }
        }

fun resultEnterValue(lot: Lot) =
        message {
            plain { text = "Прошу, поделитесь со своим слугой тайным знанием о результатах этого лота, милорд..." }
            newline(2)
            import { lotExplainAdmin(lot) }
        }

fun lotListNotification(lots: Iterable<Lot>) =
        message {
            plain { text = "Укажите, о каком лоте следует уведомить смертных, милорд..." }
            newline(2)
            import { lotList(lots) }
        }

fun lotWithNoResultNotification(lot: Lot) =
        message {
            plain { text = "Повелитель! Смею уведомить вас о том, что без Вашего вмешательства мы не узнаем результаты по лоту!" }
            newline(2)
            import { lotShort(lot, forceResult = true) }
        }