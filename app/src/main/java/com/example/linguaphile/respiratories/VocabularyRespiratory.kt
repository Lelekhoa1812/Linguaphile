package com.example.linguaphile.respiratories

import androidx.lifecycle.LiveData
import com.example.linguaphile.daos.VocabularyDAO
import com.example.linguaphile.entities.Vocabulary

import java.util.Date

class VocabularyRepository(private val vocabularyDao: VocabularyDAO) {

    val allVocabulary: LiveData<List<Vocabulary>> = vocabularyDao.getAllVocabulary()

    suspend fun insert(vocabulary: Vocabulary) {
        vocabularyDao.insertVocabulary(vocabulary)
    }

    suspend fun update(vocabulary: Vocabulary) {
        vocabularyDao.updateVocabulary(vocabulary)
    }

    suspend fun delete(vocabulary: Vocabulary) {
        vocabularyDao.deleteVocabulary(vocabulary)
    }

    fun getVocabularyByDateRange(startDate: Date, endDate: Date): LiveData<List<Vocabulary>> {
        return vocabularyDao.getVocabularyByDateRange(startDate, endDate)
    }

    fun getVocabularyByType(type: String): LiveData<List<Vocabulary>> {
        return vocabularyDao.getVocabularyByType(type)
    }

    fun getVocabularyById(vocabularyId: Int): LiveData<Vocabulary> {
        return vocabularyDao.getVocabularyById(vocabularyId)
    }

}
