package com.welfare.blood.donation.models

data class Donation(
    val recipientName: String = "",
    val location: String = "",
    val bloodType: String = "",
    val dateDonated: String = "",
    val userId: String = "",// User ID
    var status: String // Add status field

)
