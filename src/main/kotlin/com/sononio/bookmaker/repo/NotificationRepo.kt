package com.sononio.bookmaker.repo

import com.sononio.bookmaker.model.Notification
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepo : KCrudRepo<Notification, Long>