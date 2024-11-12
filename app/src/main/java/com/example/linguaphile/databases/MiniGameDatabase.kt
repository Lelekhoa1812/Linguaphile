package com.example.linguaphile.databases

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.linguaphile.daos.MiniGameDAO
import com.example.linguaphile.entities.MiniGame

@Database(entities = [MiniGame::class], version = 1, exportSchema = false)
abstract class MiniGameDatabase : RoomDatabase() {
    // Initialise DAO
    abstract fun miniGameDao(): MiniGameDAO

    companion object {
        @Volatile
        private var INSTANCE: MiniGameDatabase? = null

        // Database app
        fun getInstance(context: Context): MiniGameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MiniGameDatabase::class.java,
                    "mini_game_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
