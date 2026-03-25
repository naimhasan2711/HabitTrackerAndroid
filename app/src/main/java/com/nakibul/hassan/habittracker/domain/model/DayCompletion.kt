package com.nakibul.hassan.habittracker.domain.model

data class DayCompletion(
    val date: String,
    val totalHabits: Int,
    val completedHabits: Int,
    val completionPercentage: Float
)

