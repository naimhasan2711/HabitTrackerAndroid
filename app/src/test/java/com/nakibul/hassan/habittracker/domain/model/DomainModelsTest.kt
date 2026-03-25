package com.nakibul.hassan.habittracker.domain.model

import org.junit.Assert.*
import org.junit.Test

class DomainModelsTest {

    // ==================== Habit Model Tests ====================

    @Test
    fun `Habit creation with all parameters`() {
        val habit = Habit(
            id = "habit-1",
            name = "Exercise",
            description = "Daily workout",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = "category-1",
            frequency = "daily",
            customDays = listOf(1, 3, 5),
            reminderTime = "09:00",
            createdAt = "2024-01-01T10:00:00",
            archived = false,
            sortOrder = 1,
            currentStreak = 5,
            longestStreak = 10,
            isCompletedToday = true
        )

        assertEquals("habit-1", habit.id)
        assertEquals("Exercise", habit.name)
        assertEquals("Daily workout", habit.description)
        assertEquals("fitness", habit.icon)
        assertEquals(0xFF6200EE.toInt(), habit.color)
        assertEquals("category-1", habit.categoryId)
        assertEquals("daily", habit.frequency)
        assertEquals(listOf(1, 3, 5), habit.customDays)
        assertEquals("09:00", habit.reminderTime)
        assertEquals("2024-01-01T10:00:00", habit.createdAt)
        assertFalse(habit.archived)
        assertEquals(1, habit.sortOrder)
        assertEquals(5, habit.currentStreak)
        assertEquals(10, habit.longestStreak)
        assertTrue(habit.isCompletedToday)
    }

    @Test
    fun `Habit default values`() {
        val habit = Habit(
            id = "habit-1",
            name = "Test",
            icon = "default",
            color = 0,
            createdAt = "2024-01-01T10:00:00"
        )

        assertNull(habit.description)
        assertNull(habit.categoryId)
        assertEquals("daily", habit.frequency)
        assertNull(habit.customDays)
        assertNull(habit.reminderTime)
        assertFalse(habit.archived)
        assertEquals(0, habit.sortOrder)
        assertEquals(0, habit.currentStreak)
        assertEquals(0, habit.longestStreak)
        assertFalse(habit.isCompletedToday)
    }

    @Test
    fun `Habit equality`() {
        val habit1 = Habit("id-1", "Test", icon = "icon", color = 0, createdAt = "2024-01-01")
        val habit2 = Habit("id-1", "Test", icon = "icon", color = 0, createdAt = "2024-01-01")
        val habit3 = Habit("id-2", "Test", icon = "icon", color = 0, createdAt = "2024-01-01")

        assertEquals(habit1, habit2)
        assertNotEquals(habit1, habit3)
    }

    @Test
    fun `Habit copy modifies specified fields`() {
        val original = Habit(
            id = "habit-1",
            name = "Exercise",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            createdAt = "2024-01-01T10:00:00"
        )

        val copied = original.copy(name = "New Name", currentStreak = 10)

        assertEquals("habit-1", copied.id)
        assertEquals("New Name", copied.name)
        assertEquals("fitness", copied.icon)
        assertEquals(10, copied.currentStreak)
    }

    // ==================== HabitLog Model Tests ====================

    @Test
    fun `HabitLog creation with all parameters`() {
        val log = HabitLog(
            id = "log-1",
            habitId = "habit-1",
            date = "2024-01-15",
            completed = true,
            completedAt = "2024-01-15T10:30:00",
            notes = "Great session!"
        )

        assertEquals("log-1", log.id)
        assertEquals("habit-1", log.habitId)
        assertEquals("2024-01-15", log.date)
        assertTrue(log.completed)
        assertEquals("2024-01-15T10:30:00", log.completedAt)
        assertEquals("Great session!", log.notes)
    }

    @Test
    fun `HabitLog default values`() {
        val log = HabitLog(
            id = "log-1",
            habitId = "habit-1",
            date = "2024-01-15",
            completed = false
        )

        assertNull(log.completedAt)
        assertNull(log.notes)
    }

    @Test
    fun `HabitLog equality`() {
        val log1 = HabitLog("log-1", "habit-1", "2024-01-15", true)
        val log2 = HabitLog("log-1", "habit-1", "2024-01-15", true)
        val log3 = HabitLog("log-2", "habit-1", "2024-01-15", true)

        assertEquals(log1, log2)
        assertNotEquals(log1, log3)
    }

    // ==================== Streak Model Tests ====================

    @Test
    fun `Streak creation with all parameters`() {
        val streak = Streak(
            id = "streak-1",
            habitId = "habit-1",
            currentStreak = 7,
            longestStreak = 30,
            lastCompletedDate = "2024-01-15",
            freezeUsed = true
        )

        assertEquals("streak-1", streak.id)
        assertEquals("habit-1", streak.habitId)
        assertEquals(7, streak.currentStreak)
        assertEquals(30, streak.longestStreak)
        assertEquals("2024-01-15", streak.lastCompletedDate)
        assertTrue(streak.freezeUsed)
    }

    @Test
    fun `Streak default values`() {
        val streak = Streak(
            id = "streak-1",
            habitId = "habit-1"
        )

        assertEquals(0, streak.currentStreak)
        assertEquals(0, streak.longestStreak)
        assertNull(streak.lastCompletedDate)
        assertFalse(streak.freezeUsed)
    }

    // ==================== Category Model Tests ====================

    @Test
    fun `Category creation`() {
        val category = Category(
            id = "category-1",
            name = "Health",
            color = 0xFF4CAF50.toInt(),
            icon = "health"
        )

        assertEquals("category-1", category.id)
        assertEquals("Health", category.name)
        assertEquals(0xFF4CAF50.toInt(), category.color)
        assertEquals("health", category.icon)
    }

    @Test
    fun `Category equality`() {
        val cat1 = Category("id-1", "Health", 0, "icon")
        val cat2 = Category("id-1", "Health", 0, "icon")
        val cat3 = Category("id-2", "Health", 0, "icon")

        assertEquals(cat1, cat2)
        assertNotEquals(cat1, cat3)
    }

    // ==================== DayCompletion Model Tests ====================

    @Test
    fun `DayCompletion creation`() {
        val dayCompletion = DayCompletion(
            date = "2024-01-15",
            totalHabits = 5,
            completedHabits = 3,
            completionPercentage = 0.6f
        )

        assertEquals("2024-01-15", dayCompletion.date)
        assertEquals(5, dayCompletion.totalHabits)
        assertEquals(3, dayCompletion.completedHabits)
        assertEquals(0.6f, dayCompletion.completionPercentage, 0.001f)
    }

    @Test
    fun `DayCompletion with 100 percent completion`() {
        val dayCompletion = DayCompletion(
            date = "2024-01-15",
            totalHabits = 3,
            completedHabits = 3,
            completionPercentage = 1.0f
        )

        assertEquals(1.0f, dayCompletion.completionPercentage, 0.001f)
    }

    @Test
    fun `DayCompletion with zero habits`() {
        val dayCompletion = DayCompletion(
            date = "2024-01-15",
            totalHabits = 0,
            completedHabits = 0,
            completionPercentage = 0f
        )

        assertEquals(0, dayCompletion.totalHabits)
        assertEquals(0f, dayCompletion.completionPercentage, 0.001f)
    }

    // ==================== AnalyticsData Model Tests ====================

    @Test
    fun `AnalyticsData creation`() {
        val habitCompletions = listOf(
            HabitCompletionRate("habit-1", "Exercise", 0xFF6200EE.toInt(), 0.85f, 6, 7)
        )
        val dailyCompletions = listOf(
            DayCompletion("2024-01-15", 2, 2, 1.0f)
        )

        val analytics = AnalyticsData(
            period = 7,
            overallCompletionRate = 0.75f,
            activeHabitsCount = 5,
            bestStreak = 15,
            dailyCompletions = dailyCompletions,
            habitCompletions = habitCompletions
        )

        assertEquals(7, analytics.period)
        assertEquals(0.75f, analytics.overallCompletionRate, 0.001f)
        assertEquals(5, analytics.activeHabitsCount)
        assertEquals(15, analytics.bestStreak)
        assertEquals(1, analytics.dailyCompletions.size)
        assertEquals(1, analytics.habitCompletions.size)
    }

    // ==================== HabitCompletionRate Model Tests ====================

    @Test
    fun `HabitCompletionRate creation`() {
        val rate = HabitCompletionRate(
            habitId = "habit-1",
            habitName = "Exercise",
            habitColor = 0xFF6200EE.toInt(),
            completionRate = 0.857f,
            completedDays = 6,
            totalDays = 7
        )

        assertEquals("habit-1", rate.habitId)
        assertEquals("Exercise", rate.habitName)
        assertEquals(0xFF6200EE.toInt(), rate.habitColor)
        assertEquals(0.857f, rate.completionRate, 0.001f)
        assertEquals(6, rate.completedDays)
        assertEquals(7, rate.totalDays)
    }

    @Test
    fun `HabitCompletionRate with perfect completion`() {
        val rate = HabitCompletionRate(
            habitId = "habit-1",
            habitName = "Exercise",
            habitColor = 0,
            completionRate = 1.0f,
            completedDays = 7,
            totalDays = 7
        )

        assertEquals(1.0f, rate.completionRate, 0.001f)
        assertEquals(rate.completedDays, rate.totalDays)
    }

    @Test
    fun `HabitCompletionRate with zero completion`() {
        val rate = HabitCompletionRate(
            habitId = "habit-1",
            habitName = "Exercise",
            habitColor = 0,
            completionRate = 0f,
            completedDays = 0,
            totalDays = 7
        )

        assertEquals(0f, rate.completionRate, 0.001f)
        assertEquals(0, rate.completedDays)
    }
}
