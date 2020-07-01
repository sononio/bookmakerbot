package com.sononio.bookmaker.model

import javax.persistence.*

@Entity
@Table(name="messages")
@SequenceGenerator(name = "default_gen", sequenceName = "message_seq", allocationSize = 1)
class Message(
        @Lob var text: String,
        @Lob var answer: String? = null
) : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "chat_id")
    lateinit var chat: Chat

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null
}