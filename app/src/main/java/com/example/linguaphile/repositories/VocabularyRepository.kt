package com.example.linguaphile.repositories

import androidx.lifecycle.LiveData
import com.example.linguaphile.daos.VocabularyDAO
import com.example.linguaphile.entities.Vocabulary

import java.util.Date

class VocabularyRepository(private val vocabularyDao: VocabularyDAO) {
    // Get all Vocabulary as a list, retrieve on LiveData
    val allVocabulary: LiveData<List<Vocabulary>> = vocabularyDao.getAllVocabulary()

    // Create
    suspend fun insert(vocabulary: Vocabulary) {
        vocabularyDao.insertVocabulary(vocabulary)
    }

    // Update
    suspend fun update(vocabulary: Vocabulary) {
        vocabularyDao.updateVocabulary(vocabulary)
    }

    // Delete
    suspend fun delete(vocabulary: Vocabulary) {
        vocabularyDao.deleteVocabulary(vocabulary)
    }

    // Read by date
    fun getVocabularyByDateRange(startDate: Date, endDate: Date): LiveData<List<Vocabulary>> {
        return vocabularyDao.getVocabularyByDateRange(startDate, endDate)
    }

    // Read by type
    fun getVocabularyByType(type: String): LiveData<List<Vocabulary>> {
        return vocabularyDao.getVocabularyByType(type)
    }

    // Read by id
    fun getVocabularyById(vocabularyId: Int): LiveData<Vocabulary> {
        return vocabularyDao.getVocabularyById(vocabularyId)
    }

    // Retrieve and read all available distinct dates as a list of string
    fun getDistinctDates(): LiveData<List<Date>> {
        return vocabularyDao.getDistinctDates()
    }

}
