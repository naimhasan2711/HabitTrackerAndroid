package com.nakibul.hassan.habittracker.data.local.dao

import androidx.room.*
import com.nakibul.hassan.habittracker.data.model.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>
    
    @Query("SELECT * FROM habits WHERE archived = 1 ORDER BY name ASC")
    fun getAllArchivedHabits(): Flow<List<HabitEntity>>
    
    @Query("SELECT * FROM habits ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>
    
    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: String): HabitEntity?
    
    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitByIdFlow(id: String): Flow<HabitEntity?>
    
    @Query("SELECT * FROM habits WHERE categoryId = :categoryId AND archived = 0 ORDER BY sortOrder ASC")
    fun getHabitsByCategory(categoryId: String): Flow<List<HabitEntity>>
    
    @Query("SELECT * FROM habits WHERE archived = 0 AND reminderTime IS NOT NULL")
    suspend fun getHabitsWithReminders(): List<HabitEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)
    
    @Update
    suspend fun updateHabit(habit: HabitEntity)
    
    @Query("UPDATE habits SET archived = :archived WHERE id = :habitId")
    suspend fun updateArchiveStatus(habitId: String, archived: Boolean)
    
    @Query("UPDATE habits SET sortOrder = :sortOrder WHERE id = :habitId")
    suspend fun updateSortOrder(habitId: String, sortOrder: Int)
    
    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
    
    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: String)
    
    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
    
    @Query("SELECT COUNT(*) FROM habits WHERE archived = 0")
    fun getActiveHabitCount(): Flow<Int>
}

