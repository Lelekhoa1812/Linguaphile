package com.example.linguaphile.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val unlockImageResId: Int?, // The possible image can be unlocked with an achievement task
    val status: Int // True if completed, false otherwise, 1/0
)
