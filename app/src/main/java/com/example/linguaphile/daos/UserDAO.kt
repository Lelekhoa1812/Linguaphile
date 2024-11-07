package com.example.linguaphile.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.linguaphile.entities.User

@Dao
interface UserDao {

    // Insert a new user into the table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Update user details
    @Update
    suspend fun updateUser(user: User)

    // Get the user details (assuming a single user scenario)
    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): LiveData<User>
}
