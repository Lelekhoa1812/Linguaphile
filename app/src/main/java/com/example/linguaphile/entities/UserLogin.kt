package com.example.linguaphile.entities

import androidx.room.Entity

// Entity to keep track on number of user login days
@Entity(tableName = "user_logins")
data class UserLogin(
    val timestamp: Long // Stored as milliseconds since epoch
)
