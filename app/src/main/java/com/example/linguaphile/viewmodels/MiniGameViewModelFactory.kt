package com.example.linguaphile.viewmodels

import MiniGameViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.repositories.MiniGameRepository

// Factory setup for MiniGame ViewModel
class MiniGameViewModelFactory(private val repository: MiniGameRepository) : ViewModel() {
    // ViewModel logic
    class Factory(private val repository: MiniGameRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MiniGameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MiniGameViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
