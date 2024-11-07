package com.example.linguaphile.repositories

import androidx.lifecycle.LiveData
import com.example.linguaphile.daos.UserDao
import com.example.linguaphile.entities.User

class UserRepository(private val userDao: UserDao) {

    // Fetch all
    fun getUser(): LiveData<User> {
        return userDao.getUser()
    }

    // Update
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    // Create
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}
