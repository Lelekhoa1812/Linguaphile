package com.example.linguaphile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.linguaphile.databases.AchievementDatabase
import com.example.linguaphile.entities.Achievement
import com.example.linguaphile.repositories.AchievementRepository
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    // Initialise LiveData
    private val repository: AchievementRepository
    val allAchievements: LiveData<List<Achievement>>

    // Init Room parameters
    init {
        val achievementDao = AchievementDatabase.getInstance(application).achievementDao()
        repository = AchievementRepository(achievementDao)
        allAchievements = repository.allAchievements
    }

    // Create
    fun insert(achievement: Achievement) {
        viewModelScope.launch {
            repository.insert(achievement)
        }
    }

    // Update
    fun update(achievement: Achievement) {
        viewModelScope.launch {
            repository.update(achievement)
        }
    }
}
