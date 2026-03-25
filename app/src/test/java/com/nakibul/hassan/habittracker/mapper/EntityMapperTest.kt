package com.nakibul.hassan.habittracker.mapper

import com.nakibul.hassan.habittracker.data.model.CategoryEntity
import com.nakibul.hassan.habittracker.data.model.HabitEntity
import com.nakibul.hassan.habittracker.data.model.HabitLogEntity
import com.nakibul.hassan.habittracker.data.model.StreakEntity
import com.nakibul.hassan.habittracker.domain.model.Category
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.model.Streak
import com.nakibul.hassan.habittracker.mapper.EntityMapper.toDomain
import com.nakibul.hassan.habittracker.mapper.EntityMapper.toEntity
import org.junit.Assert.*
import org.junit.Test

class EntityMapperTest {

    // ==================== Category Mapper Tests ====================

    @Test
    fun `CategoryEntity toDomain maps correctly`() {
        // Given
        val entity = CategoryEntity(
            id = "cat-1",
            name = "Health",
            color = 0xFF6200EE.toInt(),
            icon = "heart"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("cat-1", domain.id)
        assertEquals("Health", domain.name)
        assertEquals(0xFF6200EE.toInt(), domain.color)
        assertEquals("heart", domain.icon)
    }

    @Test
    fun `Category toEntity maps correctly`() {
        // Given
        val domain = Category(
            id = "cat-1",
            name = "Health",
            color = 0xFF6200EE.toInt(),
            icon = "heart"
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("cat-1", entity.id)
        assertEquals("Health", entity.name)
        assertEquals(0xFF6200EE.toInt(), entity.color)
        assertEquals("heart", entity.icon)
    }

    // ==================== Habit Mapper Tests ====================

    @Test
    fun `HabitEntity toDomain maps basic fields correctly`() {
        // Given
        val entity = HabitEntity(
            id = "habit-1",
            name = "Exercise",
            description = "Daily workout",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = "cat-1",
            frequency = "daily",
            customDays = null,
            reminderTime = "08:00",
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 0
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("habit-1", domain.id)
        assertEquals("Exercise", domain.name)
        assertEquals("Daily workout", domain.description)
        assertEquals("fitness", domain.icon)
        assertEquals(0xFF6200EE.toInt(), domain.color)
        assertEquals("cat-1", domain.categoryId)
        assertEquals("daily", domain.frequency)
        assertEquals("08:00", domain.reminderTime)
        assertEquals("2024-01-01", domain.createdAt)
        assertFalse(domain.archived)
        assertEquals(0, domain.sortOrder)
    }

    @Test
    fun `HabitEntity toDomain uses streak data when provided`() {
        // Given
        val entity = HabitEntity(
            id = "habit-1",
            name = "Exercise",
            description = null,
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = null,
            frequency = "daily",
            customDays = null,
            reminderTime = null,
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 0
        )
        val streak = StreakEntity(
            habitId = "habit-1",
            currentStreak = 5,
            longestStreak = 10,
            lastCompletedDate = "2024-01-15"
        )

        // When
        val domain = entity.toDomain(streak = streak, isCompletedToday = true)

        // Then
        assertEquals(5, domain.currentStreak)
        assertEquals(10, domain.longestStreak)
        assertTrue(domain.isCompletedToday)
    }

    @Test
    fun `HabitEntity toDomain defaults streak to zero when not provided`() {
        // Given
        val entity = HabitEntity(
            id = "habit-1",
            name = "Exercise",
            description = null,
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = null,
            frequency = "daily",
            customDays = null,
            reminderTime = null,
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 0
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(0, domain.currentStreak)
        assertEquals(0, domain.longestStreak)
        assertFalse(domain.isCompletedToday)
    }

    @Test
    fun `HabitEntity toDomain parses customDays JSON`() {
        // Given
        val entity = HabitEntity(
            id = "habit-1",
            name = "Workout",
            description = null,
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = null,
            frequency = "custom",
            customDays = "[1,3,5]", // Monday, Wednesday, Friday
            reminderTime = null,
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 0
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertNotNull(domain.customDays)
        assertEquals(3, domain.customDays?.size)
        assertTrue(domain.customDays?.contains(1) == true)
        assertTrue(domain.customDays?.contains(3) == true)
        assertTrue(domain.customDays?.contains(5) == true)
    }

    @Test
    fun `HabitEntity toDomain handles invalid customDays JSON`() {
        // Given
        val entity = HabitEntity(
            id = "habit-1",
            name = "Workout",
            description = null,
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = null,
            frequency = "custom",
            customDays = "invalid json",
            reminderTime = null,
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 0
        )

        // When
        val domain = entity.toDomain()

        // Then - should return null for invalid JSON
        assertNull(domain.customDays)
    }

    @Test
    fun `Habit toEntity maps correctly`() {
        // Given
        val domain = Habit(
            id = "habit-1",
            name = "Exercise",
            description = "Daily workout",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = "cat-1",
            frequency = "daily",
            customDays = null,
            reminderTime = "08:00",
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 5,
            currentStreak = 10,
            longestStreak = 20,
            isCompletedToday = true
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("habit-1", entity.id)
        assertEquals("Exercise", entity.name)
        assertEquals("Daily workout", entity.description)
        assertEquals("fitness", entity.icon)
        assertEquals(0xFF6200EE.toInt(), entity.color)
        assertEquals("cat-1", entity.categoryId)
        assertEquals("daily", entity.frequency)
        assertEquals("08:00", entity.reminderTime)
        assertEquals("2024-01-01", entity.createdAt)
        assertFalse(entity.archived)
        assertEquals(5, entity.sortOrder)
    }

    @Test
    fun `Habit toEntity serializes customDays to JSON`() {
        // Given
        val domain = Habit(
            id = "habit-1",
            name = "Workout",
            description = null,
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = null,
            frequency = "custom",
            customDays = listOf(1, 3, 5),
            reminderTime = null,
            createdAt = "2024-01-01",
            archived = false,
            sortOrder = 0,
            currentStreak = 0,
            longestStreak = 0,
            isCompletedToday = false
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertNotNull(entity.customDays)
        assertTrue(entity.customDays?.contains("1") == true)
        assertTrue(entity.customDays?.contains("3") == true)
        assertTrue(entity.customDays?.contains("5") == true)
    }

    // ==================== HabitLog Mapper Tests ====================

    @Test
    fun `HabitLogEntity toDomain maps correctly`() {
        // Given
        val entity = HabitLogEntity(
            id = "log-1",
            habitId = "habit-1",
            date = "2024-01-15",
            completed = true,
            completedAt = "2024-01-15T10:30:00",
            notes = "Great workout!"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("log-1", domain.id)
        assertEquals("habit-1", domain.habitId)
        assertEquals("2024-01-15", domain.date)
        assertTrue(domain.completed)
        assertEquals("2024-01-15T10:30:00", domain.completedAt)
        assertEquals("Great workout!", domain.notes)
    }

    @Test
    fun `HabitLogEntity toDomain handles null values`() {
        // Given
        val entity = HabitLogEntity(
            id = "log-1",
            habitId = "habit-1",
            date = "2024-01-15",
            completed = false,
            completedAt = null,
            notes = null
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertFalse(domain.completed)
        assertNull(domain.completedAt)
        assertNull(domain.notes)
    }

    @Test
    fun `HabitLog toEntity maps correctly`() {
        // Given
        val domain = HabitLog(
            id = "log-1",
            habitId = "habit-1",
            date = "2024-01-15",
            completed = true,
            completedAt = "2024-01-15T10:30:00",
            notes = "Great workout!"
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("log-1", entity.id)
        assertEquals("habit-1", entity.habitId)
        assertEquals("2024-01-15", entity.date)
        assertTrue(entity.completed)
        assertEquals("2024-01-15T10:30:00", entity.completedAt)
        assertEquals("Great workout!", entity.notes)
    }

    // ==================== Streak Mapper Tests ====================

    @Test
    fun `StreakEntity toDomain maps correctly`() {
        // Given
        val entity = StreakEntity(
            id = "streak-1",
            habitId = "habit-1",
            currentStreak = 5,
            longestStreak = 10,
            lastCompletedDate = "2024-01-15",
            freezeUsed = false
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("streak-1", domain.id)
        assertEquals("habit-1", domain.habitId)
        assertEquals(5, domain.currentStreak)
        assertEquals(10, domain.longestStreak)
        assertEquals("2024-01-15", domain.lastCompletedDate)
        assertFalse(domain.freezeUsed)
    }

    @Test
    fun `StreakEntity toDomain handles null lastCompletedDate`() {
        // Given
        val entity = StreakEntity(
            id = "streak-2",
            habitId = "habit-1",
            currentStreak = 0,
            longestStreak = 5,
            lastCompletedDate = null,
            freezeUsed = true
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertNull(domain.lastCompletedDate)
        assertTrue(domain.freezeUsed)
    }

    @Test
    fun `Streak toEntity maps correctly`() {
        // Given
        val domain = Streak(
            id = "streak-1",
            habitId = "habit-1",
            currentStreak = 5,
            longestStreak = 10,
            lastCompletedDate = "2024-01-15",
            freezeUsed = false
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("streak-1", entity.id)
        assertEquals("habit-1", entity.habitId)
        assertEquals(5, entity.currentStreak)
        assertEquals(10, entity.longestStreak)
        assertEquals("2024-01-15", entity.lastCompletedDate)
        assertFalse(entity.freezeUsed)
    }

    // ==================== Round Trip Tests ====================

    @Test
    fun `Category round trip preserves data`() {
        // Given
        val original = Category(
            id = "cat-123",
            name = "Fitness",
            color = 0xFF00FF00.toInt(),
            icon = "dumbbell"
        )

        // When
        val result = original.toEntity().toDomain()

        // Then
        assertEquals(original.id, result.id)
        assertEquals(original.name, result.name)
        assertEquals(original.color, result.color)
        assertEquals(original.icon, result.icon)
    }

    @Test
    fun `HabitLog round trip preserves data`() {
        // Given
        val original = HabitLog(
            id = "log-123",
            habitId = "habit-456",
            date = "2024-06-15",
            completed = true,
            completedAt = "2024-06-15T10:00:00",
            notes = "Test notes"
        )

        // When
        val result = original.toEntity().toDomain()

        // Then
        assertEquals(original.id, result.id)
        assertEquals(original.habitId, result.habitId)
        assertEquals(original.date, result.date)
        assertEquals(original.completed, result.completed)
        assertEquals(original.completedAt, result.completedAt)
        assertEquals(original.notes, result.notes)
    }

    @Test
    fun `Streak round trip preserves data`() {
        // Given
        val original = Streak(
            id = "streak-123",
            habitId = "habit-456",
            currentStreak = 7,
            longestStreak = 14,
            lastCompletedDate = "2024-06-15",
            freezeUsed = true
        )

        // When
        val result = original.toEntity().toDomain()

        // Then
        assertEquals(original.id, result.id)
        assertEquals(original.habitId, result.habitId)
        assertEquals(original.currentStreak, result.currentStreak)
        assertEquals(original.longestStreak, result.longestStreak)
        assertEquals(original.lastCompletedDate, result.lastCompletedDate)
        assertEquals(original.freezeUsed, result.freezeUsed)
    }
}
