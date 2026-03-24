package com.abdur.rahman.habittracker.domain.usecase

import com.abdur.rahman.habittracker.domain.model.Habit
import com.abdur.rahman.habittracker.domain.model.HabitLog
import com.abdur.rahman.habittracker.domain.model.Streak
import com.abdur.rahman.habittracker.domain.repository.HabitLogRepository
import com.abdur.rahman.habittracker.domain.repository.HabitRepository
import com.abdur.rahman.habittracker.domain.repository.StreakRepository
import com.abdur.rahman.habittracker.shared.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ToggleHabitCompletionUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository,
    private val streakRepository: StreakRepository
) {
    suspend operator fun invoke(habitId: String, date: String = DateUtils.getCurrentDate()) {
        val existingLog = habitLogRepository.getLogForHabitOnDate(habitId, date)
        
        if (existingLog != null) {
            // Toggle existing log
            val newCompleted = !existingLog.completed
            habitLogRepository.updateCompletionStatus(
                habitId = habitId,
                date = date,
                completed = newCompleted,
                completedAt = if (newCompleted) DateUtils.getCurrentDateTime() else null
            )
            
            // Update streak
            updateStreak(habitId, date, newCompleted)
        } else {
            // Create new log as completed
            val newLog = HabitLog(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                date = date,
                completed = true,
                completedAt = DateUtils.getCurrentDateTime()
            )
            habitLogRepository.insertLog(newLog)
            
            // Update streak
            updateStreak(habitId, date, true)
        }
    }
    
    private suspend fun updateStreak(habitId: String, date: String, completed: Boolean) {
        val currentStreak = streakRepository.getStreakForHabit(habitId)
        
        if (currentStreak == null) {
            // Create new streak
            val newStreak = Streak(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                currentStreak = if (completed) 1 else 0,
                longestStreak = if (completed) 1 else 0,
                lastCompletedDate = if (completed) date else null
            )
            streakRepository.insertStreak(newStreak)
        } else {
            if (completed) {
                val lastDate = currentStreak.lastCompletedDate
                val isConsecutive = lastDate != null && 
                    (DateUtils.isYesterday(lastDate) || DateUtils.isToday(lastDate))
                
                val newCurrentStreak = if (isConsecutive || lastDate == null) {
                    currentStreak.currentStreak + 1
                } else {
                    1
                }
                
                val newLongestStreak = maxOf(currentStreak.longestStreak, newCurrentStreak)
                
                streakRepository.updateStreakValues(
                    habitId = habitId,
                    currentStreak = newCurrentStreak,
                    longestStreak = newLongestStreak,
                    lastCompletedDate = date
                )
            } else {
                // Don't immediately reset streak on toggle off
                // Just update the last completed date
                if (currentStreak.lastCompletedDate == date) {
                    val newCurrentStreak = maxOf(0, currentStreak.currentStreak - 1)
                    streakRepository.updateStreakValues(
                        habitId = habitId,
                        currentStreak = newCurrentStreak,
                        longestStreak = currentStreak.longestStreak,
                        lastCompletedDate = if (newCurrentStreak > 0) {
                            DateUtils.getDateMinusDays(1)
                        } else null
                    )
                }
            }
        }
    }
}

class GetActiveHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> {
        return habitRepository.getAllActiveHabits()
    }
}

class GetHabitByIdUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(id: String): Flow<Habit?> {
        return habitRepository.getHabitById(id)
    }
}

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val streakRepository: StreakRepository
) {
    suspend operator fun invoke(habit: Habit) {
        habitRepository.insertHabit(habit)
        // Create initial streak record
        val streak = Streak(
            id = UUID.randomUUID().toString(),
            habitId = habit.id,
            currentStreak = 0,
            longestStreak = 0
        )
        streakRepository.insertStreak(streak)
    }
}

class UpdateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        habitRepository.updateHabit(habit)
    }
}

class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        habitRepository.deleteHabit(habitId)
    }
}

class ArchiveHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String, archived: Boolean) {
        habitRepository.archiveHabit(habitId, archived)
    }
}
