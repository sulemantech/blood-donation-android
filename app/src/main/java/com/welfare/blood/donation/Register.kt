package com.welfare.blood.donation

data class User(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val wantToDonate: Boolean
)
