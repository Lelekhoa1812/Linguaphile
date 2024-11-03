package com.example.linguaphile.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "vocabulary")
data class Vocabulary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val date: Date,
    val meaning1: String,
    val meaning2: String? = null,
    val meaning3: String? = null,
    val meaning4: String? = null,
    val synonym1: String? = null,
    val synonym2: String? = null,
    val synonym3: String? = null,
    val synonym4: String? = null
)
