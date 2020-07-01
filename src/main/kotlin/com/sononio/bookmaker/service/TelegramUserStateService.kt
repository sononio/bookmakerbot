package com.sononio.bookmaker.service

import com.sononio.bookmaker.model.TelegramUserState
import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.repo.TelegramUserStateRepo
import org.springframework.stereotype.Service

@Service
class TelegramUserStateService(
        private val telegramUserStateRepo: TelegramUserStateRepo
) {
    fun save(userState: TelegramUserState): TelegramUserState {
        return telegramUserStateRepo.save(userState)
    }

    fun findByUser(user: User): TelegramUserState? {
        return telegramUserStateRepo.findByUser(user)
    }

    fun goToError(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.ERROR
        return telegramUserStateRepo.save(userState)
    }

    /* USER METHODS */

    fun goToRegistration(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.REGISTRATION
        return telegramUserStateRepo.save(userState)
    }

    fun goToMenu(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.MENU
        return telegramUserStateRepo.save(userState)
    }

    fun goToMakeBetId(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.MAKE_BET_ID
        return telegramUserStateRepo.save(userState)
    }

    fun goToMakeBetValue(userState: TelegramUserState, id: Long): TelegramUserState {
        userState.makeBet = id
        userState.state = TelegramUserState.State.MAKE_BET_VALUE
        return telegramUserStateRepo.save(userState)
    }

    fun goToUserBets(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.USER_BETS
        return telegramUserStateRepo.save(userState)
    }

    fun goToLotBetsId(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.LOT_BETS_ID
        return telegramUserStateRepo.save(userState)
    }

    fun goToLotBetsList(userState: TelegramUserState, id: Long): TelegramUserState {
        userState.state = TelegramUserState.State.LOT_BETS_LIST
        userState.lotBets = id
        return telegramUserStateRepo.save(userState)
    }

    /* ADMIN METHODS */

    fun goToAdminMenu(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.ADMIN_MENU
        return telegramUserStateRepo.save(userState)
    }

    fun goToStatLot(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.START_LOT
        return telegramUserStateRepo.save(userState)
    }

    fun goToShowLastLot(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.SHOW_LAST_LOT
        return telegramUserStateRepo.save(userState)
    }

    fun goToShowActiveLots(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.SHOW_ACTIVE_LOTS
        return telegramUserStateRepo.save(userState)
    }

    fun goToShowLotExplained(userState: TelegramUserState, id: Long?): TelegramUserState {
        userState.state = TelegramUserState.State.SHOW_LOT_EXPLAINED
        userState.showLotExplainedId = id
        return telegramUserStateRepo.save(userState)
    }

    fun goToResultStart(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.RESULT_START
        return telegramUserStateRepo.save(userState)
    }

    fun goToResultEnterId(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.RESULT_ENTER_ID
        return telegramUserStateRepo.save(userState)
    }

    fun goToResultEnterValue(userState: TelegramUserState, id: Long?): TelegramUserState {
        userState.state = TelegramUserState.State.RESULT_ENTER_VALUE
        userState.resultLotId = id
        return telegramUserStateRepo.save(userState)
    }

    fun goToNotifyLot(userState: TelegramUserState): TelegramUserState {
        userState.state = TelegramUserState.State.NOTIFY_LOT
        return telegramUserStateRepo.save(userState)
    }
}