package com.welfare.blood.donation.models

import java.util.Date

data class Notification(
    val title: String = "",
    val message: String = "",
    val timestamp: Date? = null,
    val clickAction: String = ""
)
