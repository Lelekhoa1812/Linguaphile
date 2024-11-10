package com.example.linguaphile.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.linguaphile.entities.Achievement

@Dao
interface AchievementDAO {
    // Create
    @Insert
    suspend fun insert(achievement: Achievement)

    // Update
    @Update
    suspend fun update(achievement: Achievement)

    // Read All
    @Query("SELECT * FROM achievement")
    fun getAllAchievements(): LiveData<List<Achievement>>

    // Read by Id
    @Query("SELECT * FROM achievement WHERE id = :id")
    suspend fun getAchievementById(id: Int): Achievement?
}
