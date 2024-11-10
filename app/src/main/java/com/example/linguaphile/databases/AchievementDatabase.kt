package com.example.linguaphile.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.linguaphile.daos.AchievementDAO
import com.example.linguaphile.daos.VocabularyDAO
import com.example.linguaphile.entities.Achievement
import com.example.linguaphile.entities.Vocabulary

@Database(entities = [Vocabulary::class, Achievement::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AchievementDatabase : RoomDatabase() {
    // Initialise DAO methods
    abstract fun vocabularyDao(): VocabularyDAO
    abstract fun achievementDao(): AchievementDAO

    companion object {
        @Volatile
        private var INSTANCE: AchievementDatabase? = null

        // Database app
        fun getInstance(context: Context): AchievementDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AchievementDatabase::class.java,
                    "achievement_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
