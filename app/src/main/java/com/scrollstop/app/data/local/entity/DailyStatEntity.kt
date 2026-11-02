package com.scrollstop.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStatEntity(
    @PrimaryKey
    val date: String, // Format: yyyy-MM-dd
    val instagramCount: Int = 0,
    val youtubeCount: Int = 0,
    val totalReels: Int = 0,
    val reminderCount: Int = 0,
    val stopCount: Int = 0,
    val continueCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
