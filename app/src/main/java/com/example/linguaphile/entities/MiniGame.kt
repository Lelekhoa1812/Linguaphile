package com.example.linguaphile.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// Entity to keep track on number of mini-game aced
@Entity(tableName = "mini_game")
data class MiniGame(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val completed: Boolean,
    val score: Int,
    val totalQuestions: Int
)

