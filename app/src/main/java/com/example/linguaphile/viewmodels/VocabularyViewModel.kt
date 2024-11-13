package com.example.linguaphile.viewmodels


import android.app.Application
import androidx.lifecycle.*
import com.example.linguaphile.databases.VocabularyDatabase
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.repositories.VocabularyRepository
import kotlinx.coroutines.launch
import java.util.Date

class VocabularyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VocabularyRepository
    val allVocabulary: LiveData<List<Vocabulary>>

    init {
        // Initialize the DAO, repository, and allVocabulary after setting up repository
        val dao = VocabularyDatabase.getDatabase(application).vocabularyDao()
        repository = VocabularyRepository(dao)
        allVocabulary = repository.allVocabulary
    }

    // Create on repository of LiveData
    fun insert(vocabulary: Vocabulary) = viewModelScope.launch {
        repository.insert(vocabulary)
    }

    // Delete on repository of LiveData
    fun delete(vocabulary: Vocabulary) = viewModelScope.launch {
        repository.delete(vocabulary)
    }

    // Update on repository of LiveData
    fun update(vocabulary: Vocabulary) = viewModelScope.launch {
        repository.update(vocabulary)
    }

    // Read by date on repository of LiveData
    fun getVocabularyByDateRange(startDate: Date, endDate: Date): LiveData<List<Vocabulary>> {
        return repository.getVocabularyByDateRange(startDate, endDate)
    }

    // Read by type on repository of LiveData
    fun getVocabularyByType(type: String): LiveData<List<Vocabulary>> {
        return repository.getVocabularyByType(type)
    }

    // Read by id on repository of LiveData
    fun getVocabularyById(vocabularyId: Int): LiveData<Vocabulary> {
        return repository.getVocabularyById(vocabularyId)
    }

    // Read and return the count of unique login days on LiveData
    // Use Transformations from LifeCycle to map distinct dates to corresponding repositories
    fun getDaysLoggedIn(): LiveData<Int> {
        return repository.getDistinctDates().map { dateList ->
            dateList.size // Return 0 if the list is null
        }
    }
}
