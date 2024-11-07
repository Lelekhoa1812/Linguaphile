package com.example.linguaphile.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.linguaphile.daos.UserDao
import com.example.linguaphile.entities.User

class UserRepository(private val userDao: UserDao) {

    // Fetch all
    fun getUser(): LiveData<User> {
        val userLiveData = userDao.getUser()
        userLiveData.observeForever { user -> // Ensure user fetched match and on pre-saved
            Log.d("UserRepository", "Fetched User: $user")
        }
        return userLiveData
    }

    // Update
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        Log.d("UserRepository", "Update successfully!") // Logs
    }

    // Create (not used since only 1 user created at all time)
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}
