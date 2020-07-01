package com.sononio.bookmaker.schedule

import com.sononio.bookmaker.service.LotService
import com.sononio.bookmaker.service.UserService
import com.sononio.bookmaker.telegram.view.resultNotification
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FinishLotSchedule(private val lotService: LotService,
                        private val userService: UserService) {
    companion object {
        val LOG by lazyLogger()
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    fun finishLots() {
        LOG.debug("Start FinishLotSchedule")
        for (lot in lotService.finishAllExpiredLots()) {
            userService.notifyAllUsers { resultNotification(lot, lot.bets.firstOrNull { bet -> bet.user == it }) }
            LotService.LOG.info("Lot finished: \"{}\"", lot.name)
        }
        LOG.debug("Stop FinishLotSchedule")
    }
}