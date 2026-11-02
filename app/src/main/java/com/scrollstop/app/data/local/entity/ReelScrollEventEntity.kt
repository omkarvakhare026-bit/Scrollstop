package com.scrollstop.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reel_scroll_events")
data class ReelScrollEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val platform: String, // "INSTAGRAM" or "YOUTUBE"
    val confidence: String, // "CONFIRMED" or "INFERRED"
    val details: String = ""
)
