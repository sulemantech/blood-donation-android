package com.welfare.blood.donation.models

data class Request(
    var id: String = "",
    var patientName: String = "",
    var phone: String = "",
    var age: Int = 0,
    var bloodType: String = "",
    var requiredUnit: Int = 0,
    var dateRequired: String = "",
    var hospital: String = "",
    var location: String = "",
    var bloodFor: String = "",
    var userId: String = "",
    var recipientId: String = "",
    var status: String = "pending",
    var critical: Boolean = false,
    val donors: List<String>? = null,
    var isDeleted: Boolean = false // Added field
)