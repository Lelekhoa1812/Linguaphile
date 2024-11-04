package com.example.linguaphile.viewmodels


import android.app.Application
import androidx.lifecycle.*
import com.example.linguaphile.databases.VocabularyDatabase
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.respiratories.VocabularyRepository
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

    fun insert(vocabulary: Vocabulary) = viewModelScope.launch {
        repository.insert(vocabulary)
    }

    fun delete(vocabulary: Vocabulary) = viewModelScope.launch {
        repository.delete(vocabulary)
    }

    fun update(vocabulary: Vocabulary) = viewModelScope.launch {
        repository.update(vocabulary)
    }

    fun getVocabularyByDateRange(startDate: Date, endDate: Date): LiveData<List<Vocabulary>> {
        return repository.getVocabularyByDateRange(startDate, endDate)
    }

    fun getVocabularyByType(type: String): LiveData<List<Vocabulary>> {
        return repository.getVocabularyByType(type)
    }

    fun getVocabularyById(vocabularyId: Int): LiveData<Vocabulary> {
        return repository.getVocabularyById(vocabularyId)
    }

}
