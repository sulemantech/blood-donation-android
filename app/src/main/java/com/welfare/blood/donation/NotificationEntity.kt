package com.welfare.blood.donation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val message: String?,
    val activity: String?,
    val extraData: String?,
    val timestamp: Long = System.currentTimeMillis()
)
