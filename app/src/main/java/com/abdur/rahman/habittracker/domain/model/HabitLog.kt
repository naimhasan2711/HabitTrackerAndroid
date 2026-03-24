package com.abdur.rahman.habittracker.domain.model

data class HabitLog(
    val id: String,
    val habitId: String,
    val date: String,
    val completed: Boolean,
    val completedAt: String? = null,
    val notes: String? = null
)
