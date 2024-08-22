package com.welfare.blood.donation.models

data class CommunityDonors(
    val userType: String = "",
    var userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    var isDeleted: Boolean = false,
)
