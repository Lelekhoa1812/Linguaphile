package com.example.linguaphile.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.daos.VocabularyDAO

@Database(entities = [Vocabulary::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)  // Register the converter here
abstract class VocabularyDatabase : RoomDatabase() {

    // DAO
    abstract fun vocabularyDao(): VocabularyDAO

    // Database app
    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        // Version control
        // Migration from version 1 to 2 to add the 'note' column
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add 'note' column with a default null value to the existing 'vocabulary' table
                db.execSQL("ALTER TABLE vocabulary ADD COLUMN note TEXT")
            }
        }

        // Init
        fun getDatabase(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database"
                )
                    //.addMigrations(MIGRATION_1_2) // Add the migration (REMOVE after use)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
