package com.nakibul.hassan.habittracker.domain.repository

import com.nakibul.hassan.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllActiveHabits(): Flow<List<Habit>>
    fun getAllArchivedHabits(): Flow<List<Habit>>
    fun getHabitById(id: String): Flow<Habit?>
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: String)
    suspend fun archiveHabit(habitId: String, archived: Boolean)
    suspend fun updateSortOrder(habitId: String, sortOrder: Int)
    fun getActiveHabitCount(): Flow<Int>
    suspend fun getHabitsWithReminders(): List<Habit>
}

