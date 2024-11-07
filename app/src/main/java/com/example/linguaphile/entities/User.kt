package com.example.linguaphile.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey val id: Int = 1, // Assuming a single user scenario
    val name: String,
    val email: String,
    val profilePicture: String // Can be a URI or path for a real implementation
)
