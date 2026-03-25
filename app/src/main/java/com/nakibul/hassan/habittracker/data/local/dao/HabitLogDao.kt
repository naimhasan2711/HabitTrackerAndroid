package com.nakibul.hassan.habittracker.data.local.dao

import androidx.room.*
import com.nakibul.hassan.habittracker.data.model.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY date DESC")
    fun getLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getLogForHabitOnDate(habitId: String, date: String): HabitLogEntity?
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    fun getLogForHabitOnDateFlow(habitId: String, date: String): Flow<HabitLogEntity?>
    
    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>>
    
    @Query("SELECT * FROM habit_logs WHERE date = :date AND completed = 1")
    fun getCompletedLogsForDate(date: String): Flow<List<HabitLogEntity>>
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsForHabitInRange(habitId: String, startDate: String, endDate: String): Flow<List<HabitLogEntity>>
    
    @Query("SELECT * FROM habit_logs WHERE date BETWEEN :startDate AND :endDate")
    fun getLogsInRange(startDate: String, endDate: String): Flow<List<HabitLogEntity>>
    
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND completed = 1 ORDER BY date DESC")
    fun getCompletedLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>
    
    @Query("SELECT COUNT(*) FROM habit_logs WHERE habitId = :habitId AND completed = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getCompletionCountForHabitInRange(habitId: String, startDate: String, endDate: String): Int
    
    @Query("SELECT COUNT(*) FROM habit_logs WHERE completed = 1 AND date = :date")
    fun getCompletionCountForDate(date: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<HabitLogEntity>)
    
    @Update
    suspend fun updateLog(log: HabitLogEntity)
    
    @Query("UPDATE habit_logs SET completed = :completed, completedAt = :completedAt WHERE habitId = :habitId AND date = :date")
    suspend fun updateCompletionStatus(habitId: String, date: String, completed: Boolean, completedAt: String?)
    
    @Delete
    suspend fun deleteLog(log: HabitLogEntity)
    
    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: String)
    
    @Query("DELETE FROM habit_logs")
    suspend fun deleteAllLogs()
    
    @Query("SELECT DISTINCT date FROM habit_logs WHERE completed = 1 AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getCompletedDatesInRange(startDate: String, endDate: String): List<String>
}

