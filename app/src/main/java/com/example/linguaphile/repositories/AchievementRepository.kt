package com.example.linguaphile.repositories

import androidx.lifecycle.LiveData
import com.example.linguaphile.daos.AchievementDAO
import com.example.linguaphile.entities.Achievement

class AchievementRepository(private val achievementDao: AchievementDAO) {

    val allAchievements: LiveData<List<Achievement>> = achievementDao.getAllAchievements()

    // Create
    suspend fun insert(achievement: Achievement) {
        achievementDao.insert(achievement)
    }

    // Update
    suspend fun update(achievement: Achievement) {
        achievementDao.update(achievement)
    }

    // Read by Id
    suspend fun getAchievementById(id: Int): Achievement? {
        return achievementDao.getAchievementById(id)
    }
}
