package com.abdur.rahman.habittracker.data.repository

import com.abdur.rahman.habittracker.data.local.dao.HabitLogDao
import com.abdur.rahman.habittracker.domain.model.HabitLog
import com.abdur.rahman.habittracker.domain.repository.HabitLogRepository
import com.abdur.rahman.habittracker.mapper.EntityMapper.toDomain
import com.abdur.rahman.habittracker.mapper.EntityMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitLogRepositoryImpl @Inject constructor(
    private val habitLogDao: HabitLogDao
) : HabitLogRepository {
    
    override fun getLogsForHabit(habitId: String): Flow<List<HabitLog>> {
        return habitLogDao.getLogsForHabit(habitId).map { logs ->
            logs.map { it.toDomain() }
        }
    }
    
    override suspend fun getLogForHabitOnDate(habitId: String, date: String): HabitLog? {
        return habitLogDao.getLogForHabitOnDate(habitId, date)?.toDomain()
    }
    
    override fun getLogForHabitOnDateFlow(habitId: String, date: String): Flow<HabitLog?> {
        return habitLogDao.getLogForHabitOnDateFlow(habitId, date).map { it?.toDomain() }
    }
    
    override fun getLogsForDate(date: String): Flow<List<HabitLog>> {
        return habitLogDao.getLogsForDate(date).map { logs ->
            logs.map { it.toDomain() }
        }
    }
    
    override fun getLogsForHabitInRange(
        habitId: String,
        startDate: String,
        endDate: String
    ): Flow<List<HabitLog>> {
        return habitLogDao.getLogsForHabitInRange(habitId, startDate, endDate).map { logs ->
            logs.map { it.toDomain() }
        }
    }
    
    override fun getLogsInRange(startDate: String, endDate: String): Flow<List<HabitLog>> {
        return habitLogDao.getLogsInRange(startDate, endDate).map { logs ->
            logs.map { it.toDomain() }
        }
    }
    
    override suspend fun getCompletionCountForHabitInRange(
        habitId: String,
        startDate: String,
        endDate: String
    ): Int {
        return habitLogDao.getCompletionCountForHabitInRange(habitId, startDate, endDate)
    }
    
    override fun getCompletionCountForDate(date: String): Flow<Int> {
        return habitLogDao.getCompletionCountForDate(date)
    }
    
    override suspend fun insertLog(log: HabitLog) {
        habitLogDao.insertLog(log.toEntity())
    }
    
    override suspend fun updateCompletionStatus(
        habitId: String,
        date: String,
        completed: Boolean,
        completedAt: String?
    ) {
        habitLogDao.updateCompletionStatus(habitId, date, completed, completedAt)
    }
    
    override suspend fun deleteLogsForHabit(habitId: String) {
        habitLogDao.deleteLogsForHabit(habitId)
    }
    
    override suspend fun getCompletedDatesInRange(startDate: String, endDate: String): List<String> {
        return habitLogDao.getCompletedDatesInRange(startDate, endDate)
    }
}
