package com.abdur.rahman.habittracker.domain.repository

import com.abdur.rahman.habittracker.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow

interface HabitLogRepository {
    fun getLogsForHabit(habitId: String): Flow<List<HabitLog>>
    suspend fun getLogForHabitOnDate(habitId: String, date: String): HabitLog?
    fun getLogForHabitOnDateFlow(habitId: String, date: String): Flow<HabitLog?>
    fun getLogsForDate(date: String): Flow<List<HabitLog>>
    fun getLogsForHabitInRange(habitId: String, startDate: String, endDate: String): Flow<List<HabitLog>>
    fun getLogsInRange(startDate: String, endDate: String): Flow<List<HabitLog>>
    suspend fun getCompletionCountForHabitInRange(habitId: String, startDate: String, endDate: String): Int
    fun getCompletionCountForDate(date: String): Flow<Int>
    suspend fun insertLog(log: HabitLog)
    suspend fun updateCompletionStatus(habitId: String, date: String, completed: Boolean, completedAt: String?)
    suspend fun deleteLogsForHabit(habitId: String)
    suspend fun getCompletedDatesInRange(startDate: String, endDate: String): List<String>
}
