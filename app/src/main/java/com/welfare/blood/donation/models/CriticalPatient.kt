package com.welfare.blood.donation.models

data class CriticalPatient(
    val patientName: String = "",
    val age: Int = 0,
    val id: String? = null,
    val bloodType: String = "",
    val requiredUnit: Int = 0,
    val dateRequired: String = "",
    val hospital: String = "",
    val location: String = "",
    val bloodFor: String = "",
    val userId: String = "", // Sender ID
    val recipientId: String = "", // Recipient ID
    var status: String = "pending", // Default status is pending
    val critical: Boolean = false
)
