package com.example.linguaphile.databases

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.linguaphile.daos.UserDao
import com.example.linguaphile.entities.User

// DB version control:
// V1: id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, profilePicture INTEGER NOT NULL
// V2: id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, profilePicture TEXT NOT NULL
// V3: id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, profilePicture TEXT
// Reset V3, V1
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    // DAO
    abstract fun userDao(): UserDao

    // Database app
    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        // Migration from version 1 to 2: Ensure profilePicture is of type TEXT
        // Migration from version 2 to 3: Ensure profilePicture is of type TEXT (nullable)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    // Create a new table with the updated schema if necessary
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS user_table_new (
                            id INTEGER PRIMARY KEY NOT NULL,
                            name TEXT NOT NULL,
                            email TEXT NOT NULL,
                            profilePicture TEXT
                        )
                        """.trimIndent()
                    )
                    // Copy data from the old table to the new table
                    db.execSQL(
                        """
                        INSERT INTO user_table_new (id, name, email, profilePicture)
                        SELECT id, name, email, CAST(profilePicture AS TEXT)
                        FROM user_table
                        """.trimIndent()
                    )
                    // Drop the old table and rename the new table
                    db.execSQL("DROP TABLE user_table")
                    db.execSQL("ALTER TABLE user_table_new RENAME TO user_table")
                } catch (e: Exception) { // Debug error logs
                    Log.e("UserDatabase", "Migration error: ${e.message}")
                }
            }
        }

        // Init
        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                )
                    //.addMigrations(MIGRATION_2_3) // Version migration (REMOVED, used for version control)
                    //.fallbackToDestructiveMigration() // Reset (REMOVED, called once, for testing)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
