package com.welfare.blood.donation.models

data class CriticalPatient(
    val patientName: String = "",
    val age: Int = 0,
    var id: String = "",
    val bloodType: String = "",
    val requiredUnit: Int = 0,
    val dateRequired: String = "",
    val hospital: String = "",
    val location: String = "",
    val bloodFor: String = "",
    val userId: String = "",
    val recipientId: String = "",
    var status: String = "pending",
    val critical: Boolean = false,
    val documentId: String="",
    val donors: List<String> = emptyList()
)
