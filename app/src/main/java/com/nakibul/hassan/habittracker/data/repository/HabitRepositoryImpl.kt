package com.nakibul.hassan.habittracker.data.repository

import com.nakibul.hassan.habittracker.data.local.dao.HabitDao
import com.nakibul.hassan.habittracker.data.local.dao.HabitLogDao
import com.nakibul.hassan.habittracker.data.local.dao.StreakDao
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.repository.HabitRepository
import com.nakibul.hassan.habittracker.mapper.EntityMapper.toDomain
import com.nakibul.hassan.habittracker.mapper.EntityMapper.toEntity
import com.nakibul.hassan.habittracker.shared.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val streakDao: StreakDao,
    private val habitLogDao: HabitLogDao
) : HabitRepository {
    
    private val today = DateUtils.getCurrentDate()
    
    override fun getAllActiveHabits(): Flow<List<Habit>> {
        return combine(
            habitDao.getAllActiveHabits(),
            streakDao.getAllStreaks(),
            habitLogDao.getLogsForDate(today)
        ) { habits, streaks, logs ->
            val streakMap = streaks.associateBy { it.habitId }
            val completedHabitIds = logs.filter { it.completed }.map { it.habitId }.toSet()
            
            habits.map { habit ->
                habit.toDomain(
                    streak = streakMap[habit.id],
                    isCompletedToday = completedHabitIds.contains(habit.id)
                )
            }
        }
    }
    
    override fun getAllArchivedHabits(): Flow<List<Habit>> {
        return combine(
            habitDao.getAllArchivedHabits(),
            streakDao.getAllStreaks()
        ) { habits, streaks ->
            val streakMap = streaks.associateBy { it.habitId }
            habits.map { habit ->
                habit.toDomain(streak = streakMap[habit.id])
            }
        }
    }
    
    override fun getHabitById(id: String): Flow<Habit?> {
        return combine(
            habitDao.getHabitByIdFlow(id),
            streakDao.getStreakForHabitFlow(id),
            habitLogDao.getLogForHabitOnDateFlow(id, today)
        ) { habit, streak, log ->
            habit?.toDomain(
                streak = streak,
                isCompletedToday = log?.completed ?: false
            )
        }
    }
    
    override suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit.toEntity())
    }
    
    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }
    
    override suspend fun deleteHabit(habitId: String) {
        habitDao.deleteHabitById(habitId)
    }
    
    override suspend fun archiveHabit(habitId: String, archived: Boolean) {
        habitDao.updateArchiveStatus(habitId, archived)
    }
    
    override suspend fun updateSortOrder(habitId: String, sortOrder: Int) {
        habitDao.updateSortOrder(habitId, sortOrder)
    }
    
    override fun getActiveHabitCount(): Flow<Int> {
        return habitDao.getActiveHabitCount()
    }
    
    override suspend fun getHabitsWithReminders(): List<Habit> {
        return habitDao.getHabitsWithReminders().map { it.toDomain() }
    }
}

