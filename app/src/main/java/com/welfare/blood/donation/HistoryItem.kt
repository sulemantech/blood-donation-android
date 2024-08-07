package com.welfare.blood.donation

data class HistoryItem(
    val donorName: String,
    val receiverName: String,
    val city: String,
    val bloodGroup: String,
    val requestStatus: String,
    val status: String,
    val bloodType: String,
    val date: String,
    val time: String
)
