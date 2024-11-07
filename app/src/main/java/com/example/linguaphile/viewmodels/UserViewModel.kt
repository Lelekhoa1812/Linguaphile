package com.example.linguaphile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linguaphile.entities.User
import com.example.linguaphile.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    // Fetch data
    fun getUser(): LiveData<User> {
        return repository.getUser()
    }

    // Update new data
    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }
}
