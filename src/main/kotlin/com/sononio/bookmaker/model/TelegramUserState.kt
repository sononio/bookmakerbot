package com.sononio.bookmaker.model

import javax.persistence.*

@Entity
@Table(name="telegram_user_state")
@SequenceGenerator(name = "default_gen", sequenceName = "telegram_user_state_seq", allocationSize = 1)
class TelegramUserState(
        @Enumerated(EnumType.STRING)
        var state: State,
        var showLotExplainedId: Long? = null,
        var resultLotId: Long? = null,
        var makeBet: Long? = null,
        var lotBets: Long? = null
) : BaseEntity<Long>() {

    @OneToOne(mappedBy = "userState")
    var user: User? = null

    enum class State {
        ERROR,
        SHOW_ACTIVE_LOTS,
        SHOW_LOT_EXPLAINED,

        REGISTRATION,
        MENU,
        MAKE_BET_ID,
        MAKE_BET_VALUE,
        USER_BETS,
        LOT_BETS_ID,
        LOT_BETS_LIST,

        ADMIN_MENU,
        START_LOT,
        SHOW_LAST_LOT,
        RESULT_START,
        RESULT_ENTER_ID,
        RESULT_ENTER_VALUE,
        NOTIFY_LOT
    }
}