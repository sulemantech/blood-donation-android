package com.welfare.blood.donation.models

data class User(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val location: String =""
)
