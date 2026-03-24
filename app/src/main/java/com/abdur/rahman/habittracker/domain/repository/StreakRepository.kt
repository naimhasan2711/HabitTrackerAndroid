package com.abdur.rahman.habittracker.domain.repository

import com.abdur.rahman.habittracker.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    suspend fun getStreakForHabit(habitId: String): Streak?
    fun getStreakForHabitFlow(habitId: String): Flow<Streak?>
    fun getAllStreaks(): Flow<List<Streak>>
    fun getBestStreak(): Flow<Int>
    suspend fun insertStreak(streak: Streak)
    suspend fun updateStreak(streak: Streak)
    suspend fun updateStreakValues(habitId: String, currentStreak: Int, longestStreak: Int, lastCompletedDate: String?)
    suspend fun updateFreezeUsed(habitId: String, freezeUsed: Boolean)
    suspend fun deleteStreakForHabit(habitId: String)
}
