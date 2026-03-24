package com.abdur.rahman.habittracker.domain.model

data class Habit(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String,
    val color: Int,
    val categoryId: String? = null,
    val frequency: String = "daily",
    val customDays: List<Int>? = null,
    val reminderTime: String? = null,
    val createdAt: String,
    val archived: Boolean = false,
    val sortOrder: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isCompletedToday: Boolean = false
)
