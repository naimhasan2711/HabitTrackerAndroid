package com.nakibul.hassan.habittracker.data.repository

import com.nakibul.hassan.habittracker.data.local.dao.StreakDao
import com.nakibul.hassan.habittracker.domain.model.Streak
import com.nakibul.hassan.habittracker.domain.repository.StreakRepository
import com.nakibul.hassan.habittracker.mapper.EntityMapper.toDomain
import com.nakibul.hassan.habittracker.mapper.EntityMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StreakRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao
) : StreakRepository {
    
    override suspend fun getStreakForHabit(habitId: String): Streak? {
        return streakDao.getStreakForHabit(habitId)?.toDomain()
    }
    
    override fun getStreakForHabitFlow(habitId: String): Flow<Streak?> {
        return streakDao.getStreakForHabitFlow(habitId).map { it?.toDomain() }
    }
    
    override fun getAllStreaks(): Flow<List<Streak>> {
        return streakDao.getAllStreaks().map { streaks ->
            streaks.map { it.toDomain() }
        }
    }
    
    override fun getBestStreak(): Flow<Int> {
        return streakDao.getBestStreak().map { it ?: 0 }
    }
    
    override suspend fun insertStreak(streak: Streak) {
        streakDao.insertStreak(streak.toEntity())
    }
    
    override suspend fun updateStreak(streak: Streak) {
        streakDao.updateStreak(streak.toEntity())
    }
    
    override suspend fun updateStreakValues(
        habitId: String,
        currentStreak: Int,
        longestStreak: Int,
        lastCompletedDate: String?
    ) {
        streakDao.updateStreakValues(habitId, currentStreak, longestStreak, lastCompletedDate)
    }
    
    override suspend fun updateFreezeUsed(habitId: String, freezeUsed: Boolean) {
        streakDao.updateFreezeUsed(habitId, freezeUsed)
    }
    
    override suspend fun deleteStreakForHabit(habitId: String) {
        streakDao.deleteStreakForHabit(habitId)
    }
}

