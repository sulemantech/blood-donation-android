package com.welfare.blood.donation

data class Register(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val location: String = "",
    val lastDonationDate: String = "",
    val isDonor: Boolean = false,
    val dateOfBirth: String = ""
)
