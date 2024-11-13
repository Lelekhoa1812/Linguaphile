package com.example.linguaphile.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.linguaphile.entities.Vocabulary
import java.util.Date

@Dao
interface VocabularyDAO {
    @Insert
    suspend fun insertVocabulary(vocabulary: Vocabulary)

    @Update
    suspend fun updateVocabulary(vocabulary: Vocabulary)

    @Delete
    suspend fun deleteVocabulary(vocabulary: Vocabulary)

    // Get item by id
    @Query("SELECT * FROM vocabulary WHERE id = :vocabularyId")
    fun getVocabularyById(vocabularyId: Int): LiveData<Vocabulary>

    // Get all item
    @Query("SELECT * FROM vocabulary ORDER BY date DESC")
    fun getAllVocabulary(): LiveData<List<Vocabulary>>

    // Get item by type
    @Query("SELECT * FROM vocabulary WHERE type = :type ORDER BY date DESC")
    fun getVocabularyByType(type: String): LiveData<List<Vocabulary>>

    // Get item by timeframe
    @Query("SELECT * FROM vocabulary WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getVocabularyByDateRange(startDate: Date, endDate: Date): LiveData<List<Vocabulary>>

    // Get distinct (all different) date to keep track on the total number of date user has logged in (by adding new vocabulary items)
    @Query("SELECT DISTINCT date FROM vocabulary")
    fun getDistinctDates(): LiveData<List<Date>>

}
