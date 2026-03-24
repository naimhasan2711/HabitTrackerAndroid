package com.abdur.rahman.habittracker.domain.usecase

import com.abdur.rahman.habittracker.domain.model.AnalyticsData
import com.abdur.rahman.habittracker.domain.model.DayCompletion
import com.abdur.rahman.habittracker.domain.model.HabitCompletionRate
import com.abdur.rahman.habittracker.domain.repository.HabitLogRepository
import com.abdur.rahman.habittracker.domain.repository.HabitRepository
import com.abdur.rahman.habittracker.domain.repository.StreakRepository
import com.abdur.rahman.habittracker.shared.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAnalyticsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val habitLogRepository: HabitLogRepository,
    private val streakRepository: StreakRepository
) {
    operator fun invoke(period: Int): Flow<AnalyticsData> {
        val endDate = DateUtils.getCurrentDate()
        val startDate = DateUtils.getDateMinusDays(period.toLong() - 1)
        
        return combine(
            habitRepository.getAllActiveHabits(),
            habitLogRepository.getLogsInRange(startDate, endDate),
            streakRepository.getBestStreak()
        ) { habits, logs, bestStreak ->
            val dates = DateUtils.getDatesInRange(startDate, endDate)
            val logsMap = logs.groupBy { it.date }
            
            // Calculate daily completions
            val dailyCompletions = dates.map { date ->
                val totalHabits = habits.size
                val completedHabits = logsMap[date]?.count { it.completed } ?: 0
                val percentage = if (totalHabits > 0) {
                    completedHabits.toFloat() / totalHabits
                } else 0f
                
                DayCompletion(
                    date = date,
                    totalHabits = totalHabits,
                    completedHabits = completedHabits,
                    completionPercentage = percentage
                )
            }
            
            // Calculate overall completion rate
            val totalPossibleCompletions = dates.size * habits.size
            val totalActualCompletions = logs.count { it.completed }
            val overallRate = if (totalPossibleCompletions > 0) {
                totalActualCompletions.toFloat() / totalPossibleCompletions
            } else 0f
            
            // Calculate per-habit completion rates
            val habitCompletions = habits.map { habit ->
                val habitLogs = logs.filter { it.habitId == habit.id }
                val completedDays = habitLogs.count { it.completed }
                val totalDays = dates.size
                val rate = if (totalDays > 0) {
                    completedDays.toFloat() / totalDays
                } else 0f
                
                HabitCompletionRate(
                    habitId = habit.id,
                    habitName = habit.name,
                    habitColor = habit.color,
                    completionRate = rate,
                    completedDays = completedDays,
                    totalDays = totalDays
                )
            }.sortedByDescending { it.completionRate }
            
            AnalyticsData(
                period = period,
                overallCompletionRate = overallRate,
                activeHabitsCount = habits.size,
                bestStreak = bestStreak,
                dailyCompletions = dailyCompletions,
                habitCompletions = habitCompletions
            )
        }
    }
}
