package com.welfare.blood.donation.models

data class ReceivedRequest(
    val patientName: String = "",
    val age: Int = 0,
    val id: String = "",
    val bloodType: String = "",
    val requiredUnit: Int = 0,
    val dateRequired: String = "",
    val hospital: String = "",
    val location: String = "",
    val bloodFor: String = "",
    val userId: String = "",
    val recipientId: String = "",
    var status: String = "pending",
    val critical: Boolean = false
)