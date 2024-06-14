package com.welfare.blood.donation.models

data class RegisterResponse(
    val role: String,
    val id: Int,
    val name: String,
    val phoneNumber: Long,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val donateBlood: Boolean
)
