package com.welfare.blood.donation.models

//data class RegisterRequest(
//        val name: String,
//        val phoneNumber: Long,
//        val email: String,
//        val password: String,
//        val dateOfBirth: String,
//        val bloodGroup: String,
//        val donateBlood: Boolean
//    )
data class UserInfo(
        val name: String,
        val phone: String,
        val email: String,
        val dob: String,
        val bloodGroup: String,
        val donateBlood: Boolean
)