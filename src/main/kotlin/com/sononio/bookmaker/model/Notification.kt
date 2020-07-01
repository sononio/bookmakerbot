package com.sononio.bookmaker.model

import com.sononio.bookmaker.model.lot.Lot
import javax.persistence.*

@Entity
@Table(name="notifications")
@SequenceGenerator(name = "default_gen", sequenceName = "notification_seq", allocationSize = 1)
class Notification(
        @Enumerated(EnumType.STRING)
        var type: Type
) : BaseEntity<Long>() {

    @ManyToOne
    @JoinColumn("lot_id")
    lateinit var lot: Lot

    enum class Type {
        END_BET_NOTIFICATION
    }
}