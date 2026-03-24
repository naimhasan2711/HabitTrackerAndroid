package com.abdur.rahman.habittracker.domain.model

data class Streak(
    val id: String,
    val habitId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: String? = null,
    val freezeUsed: Boolean = false
)
