package com.abdur.rahman.habittracker.mapper

import com.abdur.rahman.habittracker.data.model.*
import com.abdur.rahman.habittracker.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EntityMapper {
    
    private val gson = Gson()
    
    // Category mappers
    fun CategoryEntity.toDomain(): Category = Category(
        id = id,
        name = name,
        color = color,
        icon = icon
    )
    
    fun Category.toEntity(): CategoryEntity = CategoryEntity(
        id = id,
        name = name,
        color = color,
        icon = icon
    )
    
    // Habit mappers
    fun HabitEntity.toDomain(streak: StreakEntity? = null, isCompletedToday: Boolean = false): Habit = Habit(
        id = id,
        name = name,
        description = description,
        icon = icon,
        color = color,
        categoryId = categoryId,
        frequency = frequency,
        customDays = customDays?.let { parseCustomDays(it) },
        reminderTime = reminderTime,
        createdAt = createdAt,
        archived = archived,
        sortOrder = sortOrder,
        currentStreak = streak?.currentStreak ?: 0,
        longestStreak = streak?.longestStreak ?: 0,
        isCompletedToday = isCompletedToday
    )
    
    fun Habit.toEntity(): HabitEntity = HabitEntity(
        id = id,
        name = name,
        description = description,
        icon = icon,
        color = color,
        categoryId = categoryId,
        frequency = frequency,
        customDays = customDays?.let { gson.toJson(it) },
        reminderTime = reminderTime,
        createdAt = createdAt,
        archived = archived,
        sortOrder = sortOrder
    )
    
    private fun parseCustomDays(json: String): List<Int>? {
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    // HabitLog mappers
    fun HabitLogEntity.toDomain(): HabitLog = HabitLog(
        id = id,
        habitId = habitId,
        date = date,
        completed = completed,
        completedAt = completedAt,
        notes = notes
    )
    
    fun HabitLog.toEntity(): HabitLogEntity = HabitLogEntity(
        id = id,
        habitId = habitId,
        date = date,
        completed = completed,
        completedAt = completedAt,
        notes = notes
    )
    
    // Streak mappers
    fun StreakEntity.toDomain(): Streak = Streak(
        id = id,
        habitId = habitId,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastCompletedDate = lastCompletedDate,
        freezeUsed = freezeUsed
    )
    
    fun Streak.toEntity(): StreakEntity = StreakEntity(
        id = id,
        habitId = habitId,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastCompletedDate = lastCompletedDate,
        freezeUsed = freezeUsed
    )
}
