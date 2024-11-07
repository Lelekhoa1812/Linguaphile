package com.example.linguaphile.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.daos.VocabularyDAO

@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)  // Register the converter here
abstract class VocabularyDatabase : RoomDatabase() {

    // DAO
    abstract fun vocabularyDao(): VocabularyDAO

    // Database app
    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getDatabase(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
