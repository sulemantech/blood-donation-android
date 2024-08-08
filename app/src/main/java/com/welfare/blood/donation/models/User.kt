package com.welfare.blood.donation.models

data class User(
    val userId: String = "",
    val name: String = "",
    val bloodGroup: String = "",
    val phone: String = "",
    val email: String = ""
)
