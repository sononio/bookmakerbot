package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.Message
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo : KCrudRepo<Message, Int>