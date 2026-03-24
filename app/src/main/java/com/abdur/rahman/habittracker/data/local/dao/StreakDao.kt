package com.abdur.rahman.habittracker.data.local.dao

import androidx.room.*
import com.abdur.rahman.habittracker.data.model.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    
    @Query("SELECT * FROM streaks WHERE habitId = :habitId LIMIT 1")
    suspend fun getStreakForHabit(habitId: String): StreakEntity?
    
    @Query("SELECT * FROM streaks WHERE habitId = :habitId LIMIT 1")
    fun getStreakForHabitFlow(habitId: String): Flow<StreakEntity?>
    
    @Query("SELECT * FROM streaks")
    fun getAllStreaks(): Flow<List<StreakEntity>>
    
    @Query("SELECT MAX(longestStreak) FROM streaks")
    fun getBestStreak(): Flow<Int?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreaks(streaks: List<StreakEntity>)
    
    @Update
    suspend fun updateStreak(streak: StreakEntity)
    
    @Query("UPDATE streaks SET currentStreak = :currentStreak, longestStreak = :longestStreak, lastCompletedDate = :lastCompletedDate WHERE habitId = :habitId")
    suspend fun updateStreakValues(habitId: String, currentStreak: Int, longestStreak: Int, lastCompletedDate: String?)
    
    @Query("UPDATE streaks SET freezeUsed = :freezeUsed WHERE habitId = :habitId")
    suspend fun updateFreezeUsed(habitId: String, freezeUsed: Boolean)
    
    @Delete
    suspend fun deleteStreak(streak: StreakEntity)
    
    @Query("DELETE FROM streaks WHERE habitId = :habitId")
    suspend fun deleteStreakForHabit(habitId: String)
    
    @Query("DELETE FROM streaks")
    suspend fun deleteAllStreaks()
}
