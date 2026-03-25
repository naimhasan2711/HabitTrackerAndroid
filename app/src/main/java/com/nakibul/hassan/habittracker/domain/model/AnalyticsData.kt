package com.nakibul.hassan.habittracker.domain.model

data class AnalyticsData(
    val period: Int,
    val overallCompletionRate: Float,
    val activeHabitsCount: Int,
    val bestStreak: Int,
    val dailyCompletions: List<DayCompletion>,
    val habitCompletions: List<HabitCompletionRate>
)

data class HabitCompletionRate(
    val habitId: String,
    val habitName: String,
    val habitColor: Int,
    val completionRate: Float,
    val completedDays: Int,
    val totalDays: Int
)

