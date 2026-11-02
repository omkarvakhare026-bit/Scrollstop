package com.scrollstop.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motivational_items")
data class MotivationalItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String? = null, // Internal storage path or null for gradient banner
    val quote: String,
    val author: String = "Discipline",
    val isUserAdded: Boolean = false,
    val isActive: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
