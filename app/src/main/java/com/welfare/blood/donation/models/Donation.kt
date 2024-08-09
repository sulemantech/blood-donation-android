package com.welfare.blood.donation.models

data class Donation(
    val recipientName: String = "",
    val location: String = "",
    val bloodType: String = "",
    val date: String = "",
    var id: String? = null,
    var userId: String? = null,
    var status: String? = "pending",
    var isDeleted: Boolean = false // Added field

)