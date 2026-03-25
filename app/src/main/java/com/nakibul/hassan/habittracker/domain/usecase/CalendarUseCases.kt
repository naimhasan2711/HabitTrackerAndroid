package com.nakibul.hassan.habittracker.domain.usecase

import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.repository.HabitLogRepository
import com.nakibul.hassan.habittracker.shared.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

data class CalendarDay(
    val date: String,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val completionPercentage: Float,
    val completedCount: Int,
    val totalCount: Int
)

class GetCalendarDataUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository
) {
    operator fun invoke(month: LocalDate, totalHabits: Int): Flow<List<HabitLog>> {
        val startOfMonth = DateUtils.getStartOfMonth(month)
        val endOfMonth = DateUtils.getEndOfMonth(month)
        
        // Extend to include days from previous/next month to fill calendar grid
        val startDate = startOfMonth.minusDays(startOfMonth.dayOfWeek.value.toLong() - 1)
        val endDate = endOfMonth.plusDays(7 - endOfMonth.dayOfWeek.value.toLong())
        
        return habitLogRepository.getLogsInRange(
            DateUtils.formatDate(startDate),
            DateUtils.formatDate(endDate)
        )
    }
}

class GetHabitLogsForDateUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository
) {
    operator fun invoke(date: String): Flow<List<HabitLog>> {
        return habitLogRepository.getLogsForDate(date)
    }
}

class GetHabitLogsInRangeUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository
) {
    operator fun invoke(habitId: String, startDate: String, endDate: String): Flow<List<HabitLog>> {
        return habitLogRepository.getLogsForHabitInRange(habitId, startDate, endDate)
    }
}

