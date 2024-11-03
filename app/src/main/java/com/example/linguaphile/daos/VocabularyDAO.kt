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
    @Query("SELECT * FROM vocabulary WHERE id = :id")
    fun getVocabularyById(id: Int): LiveData<Vocabulary>

    // Get all item
    @Query("SELECT * FROM vocabulary ORDER BY date DESC")
    fun getAllVocabulary(): LiveData<List<Vocabulary>>

    // Get item by type
    @Query("SELECT * FROM vocabulary WHERE type = :type ORDER BY date DESC")
    fun getVocabularyByType(type: String): LiveData<List<Vocabulary>>

    // Get item by timeframe
    @Query("SELECT * FROM vocabulary WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getVocabularyByDateRange(startDate: Date, endDate: Date): LiveData<List<Vocabulary>>
}
