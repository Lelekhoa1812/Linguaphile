package com.example.linguaphile.databases

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.linguaphile.daos.UserDao
import com.example.linguaphile.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 4th version is just a fresh data initialisation of 3th db version
// Check the 11th commit for version control 1-2-3 and migration instruction
@Database(entities = [User::class], version = 4, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    // DAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        // Init parameters for setups
        private var INSTANCE: UserDatabase? = null
        private const val PREF_NAME = "UserDatabasePrefs"
        private const val INITIAL_DATA_INSERTED_KEY = "initialDataInserted" // Initial user to be inserted

        // Database app
        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                )
                    .addCallback(UserDatabaseCallback(context)) // Callback with context if there is no existed user
                    //.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4) // Add migrations between versions if needed (REMOVED)
                    .fallbackToDestructiveMigration() // Reset data (REMOVED if not testing)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback for default user upon creating the database (db)
    private class UserDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        // Trigger default data insertion on SQLite
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("UserDatabase", "onCreate callback triggered")
            CoroutineScope(Dispatchers.IO).launch {
                if (!isInitialDataInserted(context)) {
                    INSTANCE?.let { database ->
                        insertInitialData(database.userDao())
                        markInitialDataInserted(context)
                    }
                }
            }
        }

        // Boolean tracker if initial data has been configured
        private fun isInitialDataInserted(context: Context): Boolean {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(INITIAL_DATA_INSERTED_KEY, false)
        }

        // Mark initial data has insertion tracker on to the shared preference
        private fun markInitialDataInserted(context: Context) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(INITIAL_DATA_INSERTED_KEY, true).apply()
        }

        // Initialise and call insert default user (User Name) upon firstly crate the database
        private suspend fun insertInitialData(userDao: UserDao) {
            // Try to create, else fails and catch error
            try {
                val initialUser = User(id = 1, name = "User Name", email = "user@example.com", profilePicture = null)
                userDao.insertUser(initialUser)
                Log.d("UserDatabase", "Initial user inserted: $initialUser")
            } catch (e: Exception) {
                Log.e("UserDatabase", "Error inserting initial user: ${e.message}")
            }
        }
    }
}
