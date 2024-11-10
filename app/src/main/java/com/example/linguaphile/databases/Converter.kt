package com.example.linguaphile.databases

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    // Convert from to timestamp with correct destination format
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
