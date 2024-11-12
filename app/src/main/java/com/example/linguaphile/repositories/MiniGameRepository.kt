package com.example.linguaphile.repositories

import androidx.lifecycle.LiveData
import com.example.linguaphile.daos.MiniGameDAO
import com.example.linguaphile.entities.MiniGame

class MiniGameRepository(private val miniGameDao: MiniGameDAO) {
    // Count complete aced Mini Game
    fun getCompletedGamesCount(): LiveData<Int> {
        return miniGameDao.getCompletedGamesCount()
    }

    // Create
    suspend fun insertMiniGame(miniGame: MiniGame) {
        miniGameDao.insertMiniGame(miniGame)
    }

    // Read
    fun getAllMiniGames(): LiveData<List<MiniGame>> {
        return miniGameDao.getAllMiniGames()
    }
}
