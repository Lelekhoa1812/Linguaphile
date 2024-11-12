package com.example.linguaphile.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.linguaphile.entities.MiniGame

@Dao
interface MiniGameDAO {
    // Create
    @Insert
    suspend fun insertMiniGame(miniGame: MiniGame)

    // Count
    @Query("SELECT COUNT(*) FROM mini_game WHERE completed = 1")
    fun getCompletedGamesCount(): LiveData<Int>

    // Read
    @Query("SELECT * FROM mini_game")
    fun getAllMiniGames(): LiveData<List<MiniGame>>
}
