package com.sononio.bookmaker.util

import com.sononio.bookmaker.model.Message
import com.sononio.bookmaker.model.TelegramUserState
import com.sononio.bookmaker.model.User
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.service.BetService
import com.sononio.bookmaker.service.LotService
import com.sononio.bookmaker.service.TelegramUserStateService
import com.sononio.bookmaker.service.UserService
import com.sononio.bookmaker.telegram.TelegramFindEntityException
import com.sononio.bookmaker.telegram.TelegramLotIsNotActiveException
import com.sononio.bookmaker.telegram.view.*
import com.sononio.bookmaker.util.logging.lazyLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class StateMachine(
        private val lotService: LotService,
        private val telegramUserStateService: TelegramUserStateService,
        private val userService: UserService,
        private val betService: BetService
) {
    companion object { val LOG by lazyLogger() }

    @Value("\${telegram.invite_code}")
    private lateinit var inviteCode: String

    fun proceedMessage(message: Message) {
        if (message.user!!.userState.state == TelegramUserState.State.REGISTRATION) {
            if (message.text != inviteCode) return
            userService.approve(message.user!!)
            telegramUserStateService.goToMenu(message.user!!.userState)
            LOG.info("Registered user: {}", message.user!!.name)
        }

        when (message.text) {
            "/start", "/menu" -> {telegramUserStateService.goToMenu(message.user!!.userState); return}
            "/showlots" -> {telegramUserStateService.goToShowActiveLots(message.user!!.userState); return}
            "/bet" -> {telegramUserStateService.goToMakeBetId(message.user!!.userState); return}
            "/mybets" -> {telegramUserStateService.goToUserBets(message.user!!.userState); return}
            "/lotbets" -> {telegramUserStateService.goToLotBetsId(message.user!!.userState); return}
        }

        when (message.user!!.userState.state) {
            TelegramUserState.State.SHOW_ACTIVE_LOTS -> {
                telegramUserStateService.goToShowLotExplained(message.user!!.userState, message.text.toLong()) }
            TelegramUserState.State.MAKE_BET_ID -> {
                lateinit var lot: Lot
                try {
                    lot = lotService.findById(message.text.toLong())!!
                } catch (e: Exception) {
                    throw TelegramFindEntityException(e)
                }
                if (lot.status != Lot.Status.ACTIVE) throw TelegramLotIsNotActiveException()

                telegramUserStateService.goToMakeBetValue(message.user!!.userState, lot.getId()!!)
            }
            TelegramUserState.State.MAKE_BET_VALUE -> {
                lateinit var lot: Lot
                try {
                    lot = lotService.findById(message.user!!.userState.makeBet!!)!!
                } catch (e: Exception) {
                    throw TelegramFindEntityException(e)
                }
                if (lot.status != Lot.Status.ACTIVE) throw TelegramLotIsNotActiveException()
                betService.createOrUpdateBet(message.user!!, lot, message.text)

                telegramUserStateService.goToUserBets(message.user!!.userState)
            }
            TelegramUserState.State.LOT_BETS_ID -> {
                lateinit var lot: Lot
                try {
                    lot = lotService.findById(message.text.toLong())!!
                } catch (e: Exception) {
                    throw TelegramFindEntityException(e)
                }

                telegramUserStateService.goToLotBetsList(message.user!!.userState, lot.getId()!!)
            }

            else -> {}
        }
    }

    fun proceedAdminMessage(message: Message) {
        when (message.text) {
            "/start", "/menu" -> {telegramUserStateService.goToAdminMenu(message.user!!.userState); return}
            "/startlot" -> {telegramUserStateService.goToStatLot(message.user!!.userState); return}
            "/editlot" -> {telegramUserStateService.goToEditLotId(message.user!!.userState); return}
            "/showlots" -> {telegramUserStateService.goToShowActiveLots(message.user!!.userState); return}
            "/result" -> {telegramUserStateService.goToResultStart(message.user!!.userState); return}
            "/notify" -> {telegramUserStateService.goToNotify(message.user!!.userState); return}
            "/notifylot" -> {telegramUserStateService.goToNotifyLot(message.user!!.userState); return}
        }

        when (message.user!!.userState.state) {
            TelegramUserState.State.START_LOT -> {
                lotService.createLotFromAdmin(lotService.parseLot(message.text))
                telegramUserStateService.goToShowLastLot(message.user!!.userState) }
            TelegramUserState.State.EDIT_LOT_ID -> {
                telegramUserStateService.goToEditLotValue(message.user!!.userState, message.text.toLong()) }
            TelegramUserState.State.EDIT_LOT_VALUE -> {
                lotService.createLotFromAdmin(lotService.parseLot(message.text,
                        lotService.findById(message.user!!.userState.editLotId!!)))
                telegramUserStateService.goToShowLotExplained(message.user!!.userState,
                        message.user!!.userState.editLotId) }
            TelegramUserState.State.SHOW_ACTIVE_LOTS -> {
                telegramUserStateService.goToShowLotExplained(message.user!!.userState, message.text.toLong()) }
            TelegramUserState.State.RESULT_ENTER_ID -> {
                telegramUserStateService.goToResultEnterValue(message.user!!.userState, message.text.toLong()) }
            TelegramUserState.State.RESULT_ENTER_VALUE -> {
                lotService.updateLotResult(lotService.findById(message.user!!.userState.resultLotId!!)!!, message.text)
                telegramUserStateService.goToShowLotExplained(
                        message.user!!.userState, message.user!!.userState.resultLotId) }
            TelegramUserState.State.NOTIFY -> {
                userService.notifyAllUsers(customNotification(message.text))
                telegramUserStateService.goToAdminMenu(message.user!!.userState) }
            TelegramUserState.State.NOTIFY_LOT -> {
                userService.notifyAllUsersAboutLot(lotService.findById(message.text.toLong())!!)
                telegramUserStateService.goToAdminMenu(message.user!!.userState) }
            else -> {}
        }
    }

    fun genMessage(user: User): String = when (user.userState.state) {
        TelegramUserState.State.REGISTRATION -> registration.toString()
        TelegramUserState.State.MENU -> menu(lotService.findActiveLotsCount()).toString()
        TelegramUserState.State.SHOW_ACTIVE_LOTS -> lotListShowUser(lotService.findNotFinishedLots()).toString()
        TelegramUserState.State.SHOW_LOT_EXPLAINED -> {
            val lot = lotService.findById(user.userState.showLotExplainedId!!)
            when (lot) {
                null -> lotNotFound.toString()
                else -> {
                    telegramUserStateService.goToMenu(user.userState)
                    lotExplainCommon(lotService.findById(user.userState.showLotExplainedId!!)!!).toString()
                }
            }
        }
        TelegramUserState.State.MAKE_BET_ID -> makeBetId(lotService.findActiveLots()).toString()
        TelegramUserState.State.MAKE_BET_VALUE ->
            makeBetValue(lotService.findById(user.userState.makeBet!!)!!).toString()
        TelegramUserState.State.USER_BETS -> userBets(user).toString()
        TelegramUserState.State.LOT_BETS_ID -> lotListShowUser(lotService.findActiveLots()).toString()
        TelegramUserState.State.LOT_BETS_LIST -> {
            telegramUserStateService.goToMenu(user.userState)
            lotBetsList(lotService.findById(user.userState.lotBets!!)!!).toString() }

        else -> {
            telegramUserStateService.goToMenu(user.userState); genMessage(user)
        }
    }

    fun genAdminMessage(user: User): String = when (user.userState.state) {
        TelegramUserState.State.ADMIN_MENU -> adminMenuMessage.toString()
        TelegramUserState.State.START_LOT -> startLotMessage.toString()
        TelegramUserState.State.EDIT_LOT_ID -> editLoSelectId(lotService.findActiveLots()).toString()
        TelegramUserState.State.EDIT_LOT_VALUE -> editLotMessage.toString()
        TelegramUserState.State.SHOW_LAST_LOT -> {
            telegramUserStateService.goToAdminMenu(user.userState)
            lotExplainAdmin(lotService.findLastLot()).toString()
        }
        TelegramUserState.State.SHOW_ACTIVE_LOTS -> {
            lotListShow(lotService.findActiveLots()).toString()
        }
        TelegramUserState.State.RESULT_START -> {
            telegramUserStateService.goToResultEnterId(user.userState)
            lotListResult(lotService.findActiveLots()).toString()
        }
        TelegramUserState.State.RESULT_ENTER_VALUE -> {
            resultEnterValue(lotService.findById(user.userState.resultLotId!!)!!).toString()
        }
        TelegramUserState.State.SHOW_LOT_EXPLAINED -> {
            val lot = lotService.findById(user.userState.showLotExplainedId!!)
            when (lot) {
                null -> lotNotFound.toString()
                else -> {
                    telegramUserStateService.goToAdminMenu(user.userState)
                    lotExplainAdmin(lotService.findById(user.userState.showLotExplainedId!!)!!).toString()
                }
            }
        }
        TelegramUserState.State.NOTIFY -> {
            notificationEnter().toString()
        }
        TelegramUserState.State.NOTIFY_LOT -> {
            lotListNotification(lotService.findActiveLots()).toString()
        }

        else -> {
            telegramUserStateService.goToAdminMenu(user.userState); genAdminMessage(user) }
    }
}