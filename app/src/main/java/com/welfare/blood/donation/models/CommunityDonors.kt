package com.welfare.blood.donation.models

import com.google.firebase.Timestamp
import java.util.Date

data class CommunityDonors(
    val userType: String = "",
    var userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    var isDeleted: Boolean = false,
    val registrationTimestamp: Timestamp? = null
)
