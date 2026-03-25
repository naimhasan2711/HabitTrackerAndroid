package com.nakibul.hassan.habittracker.data.repository

import com.nakibul.hassan.habittracker.data.local.dao.HabitDao
import com.nakibul.hassan.habittracker.data.local.dao.HabitLogDao
import com.nakibul.hassan.habittracker.data.local.dao.StreakDao
import com.nakibul.hassan.habittracker.data.model.HabitEntity
import com.nakibul.hassan.habittracker.data.model.HabitLogEntity
import com.nakibul.hassan.habittracker.data.model.StreakEntity
import com.nakibul.hassan.habittracker.domain.model.Habit
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HabitRepositoryImplTest {

    @MockK
    private lateinit var habitDao: HabitDao

    @MockK
    private lateinit var streakDao: StreakDao

    @MockK
    private lateinit var habitLogDao: HabitLogDao

    private lateinit var repository: HabitRepositoryImpl

    private val testHabitEntity = HabitEntity(
        id = "habit-1",
        name = "Exercise",
        description = "Daily workout",
        categoryId = "cat-1",
        reminderTime = "08:00",
        frequency = "daily",
        color = 0xFF6200EE.toInt(),
        icon = "fitness",
        archived = false,
        createdAt = "2024-01-01",
        sortOrder = 0
    )

    private val archivedHabitEntity = HabitEntity(
        id = "habit-2",
        name = "Old Reading",
        description = "Archived habit",
        categoryId = "cat-2",
        reminderTime = null,
        frequency = "daily",
        color = 0xFF03DAC5.toInt(),
        icon = "book",
        archived = true,
        createdAt = "2024-01-01",
        sortOrder = 1
    )

    private val testStreakEntity = StreakEntity(
        id = "streak-1",
        habitId = "habit-1",
        currentStreak = 5,
        longestStreak = 10,
        lastCompletedDate = "2024-01-15"
    )

    private val testLogEntity = HabitLogEntity(
        id = "log-1",
        habitId = "habit-1",
        date = "2024-01-15",
        completed = true,
        completedAt = "2024-01-15T10:00:00"
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = HabitRepositoryImpl(habitDao, streakDao, habitLogDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== Get All Active Habits Tests ====================

    @Test
    fun `getAllActiveHabits returns habits with streaks`() = runTest {
        // Given
        every { habitDao.getAllActiveHabits() } returns flowOf(listOf(testHabitEntity))
        every { streakDao.getAllStreaks() } returns flowOf(listOf(testStreakEntity))
        every { habitLogDao.getLogsForDate(any()) } returns flowOf(listOf(testLogEntity))

        // When
        val result = repository.getAllActiveHabits().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Exercise", result[0].name)
        assertEquals(5, result[0].currentStreak)
        assertEquals(10, result[0].longestStreak)
    }

    @Test
    fun `getAllActiveHabits marks completed habits`() = runTest {
        // Given
        every { habitDao.getAllActiveHabits() } returns flowOf(listOf(testHabitEntity))
        every { streakDao.getAllStreaks() } returns flowOf(listOf(testStreakEntity))
        every { habitLogDao.getLogsForDate(any()) } returns flowOf(listOf(testLogEntity))

        // When
        val result = repository.getAllActiveHabits().first()

        // Then
        assertTrue(result[0].isCompletedToday)
    }

    @Test
    fun `getAllActiveHabits returns empty list when no habits`() = runTest {
        // Given
        every { habitDao.getAllActiveHabits() } returns flowOf(emptyList())
        every { streakDao.getAllStreaks() } returns flowOf(emptyList())
        every { habitLogDao.getLogsForDate(any()) } returns flowOf(emptyList())

        // When
        val result = repository.getAllActiveHabits().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllActiveHabits handles missing streaks`() = runTest {
        // Given
        every { habitDao.getAllActiveHabits() } returns flowOf(listOf(testHabitEntity))
        every { streakDao.getAllStreaks() } returns flowOf(emptyList())
        every { habitLogDao.getLogsForDate(any()) } returns flowOf(emptyList())

        // When
        val result = repository.getAllActiveHabits().first()

        // Then
        assertEquals(1, result.size)
        assertEquals(0, result[0].currentStreak)
    }

    // ==================== Get All Archived Habits Tests ====================

    @Test
    fun `getAllArchivedHabits returns archived habits`() = runTest {
        // Given
        every { habitDao.getAllArchivedHabits() } returns flowOf(listOf(archivedHabitEntity))
        every { streakDao.getAllStreaks() } returns flowOf(emptyList())

        // When
        val result = repository.getAllArchivedHabits().first()

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].archived)
    }

    @Test
    fun `getAllArchivedHabits returns empty list when no archived`() = runTest {
        // Given
        every { habitDao.getAllArchivedHabits() } returns flowOf(emptyList())
        every { streakDao.getAllStreaks() } returns flowOf(emptyList())

        // When
        val result = repository.getAllArchivedHabits().first()

        // Then
        assertTrue(result.isEmpty())
    }

    // ==================== Get Habit By Id Tests ====================

    @Test
    fun `getHabitById returns habit with streak info`() = runTest {
        // Given
        every { habitDao.getHabitByIdFlow("habit-1") } returns flowOf(testHabitEntity)
        every { streakDao.getStreakForHabitFlow("habit-1") } returns flowOf(testStreakEntity)
        every { habitLogDao.getLogForHabitOnDateFlow("habit-1", any()) } returns flowOf(testLogEntity)

        // When
        val result = repository.getHabitById("habit-1").first()

        // Then
        assertNotNull(result)
        assertEquals("habit-1", result?.id)
        assertEquals(5, result?.currentStreak)
    }

    @Test
    fun `getHabitById returns null when not found`() = runTest {
        // Given
        every { habitDao.getHabitByIdFlow("non-existent") } returns flowOf(null)
        every { streakDao.getStreakForHabitFlow("non-existent") } returns flowOf(null)
        every { habitLogDao.getLogForHabitOnDateFlow("non-existent", any()) } returns flowOf(null)

        // When
        val result = repository.getHabitById("non-existent").first()

        // Then
        assertNull(result)
    }

    // ==================== Insert Habit Tests ====================

    @Test
    fun `insertHabit calls dao insert`() = runTest {
        // Given
        val habit = Habit(
            id = "new-habit",
            name = "New Habit",
            description = "Test",
            icon = "default",
            color = 0xFF6200EE.toInt(),
            categoryId = "cat-1",
            reminderTime = null,
            frequency = "daily",
            archived = false,
            createdAt = "2024-01-01T10:00:00"
        )
        coEvery { habitDao.insertHabit(any()) } just Runs

        // When
        repository.insertHabit(habit)

        // Then
        coVerify { habitDao.insertHabit(match { it.id == "new-habit" }) }
    }

    // ==================== Update Habit Tests ====================

    @Test
    fun `updateHabit calls dao update`() = runTest {
        // Given
        val habit = Habit(
            id = "habit-1",
            name = "Updated Exercise",
            description = "Updated workout",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = "cat-1",
            reminderTime = "09:00",
            frequency = "daily",
            archived = false,
            createdAt = "2024-01-01T10:00:00"
        )
        coEvery { habitDao.updateHabit(any()) } just Runs

        // When
        repository.updateHabit(habit)

        // Then
        coVerify { habitDao.updateHabit(match { it.name == "Updated Exercise" }) }
    }

    // ==================== Delete Habit Tests ====================

    @Test
    fun `deleteHabit calls dao delete`() = runTest {
        // Given
        coEvery { habitDao.deleteHabitById("habit-1") } just Runs

        // When
        repository.deleteHabit("habit-1")

        // Then
        coVerify { habitDao.deleteHabitById("habit-1") }
    }

    // ==================== Archive Habit Tests ====================

    @Test
    fun `archiveHabit calls dao with archive true`() = runTest {
        // Given
        coEvery { habitDao.updateArchiveStatus("habit-1", true) } just Runs

        // When
        repository.archiveHabit("habit-1", true)

        // Then
        coVerify { habitDao.updateArchiveStatus("habit-1", true) }
    }

    @Test
    fun `archiveHabit calls dao with archive false`() = runTest {
        // Given
        coEvery { habitDao.updateArchiveStatus("habit-1", false) } just Runs

        // When
        repository.archiveHabit("habit-1", false)

        // Then
        coVerify { habitDao.updateArchiveStatus("habit-1", false) }
    }

    // ==================== Update Sort Order Tests ====================

    @Test
    fun `updateSortOrder calls dao`() = runTest {
        // Given
        coEvery { habitDao.updateSortOrder("habit-1", 5) } just Runs

        // When
        repository.updateSortOrder("habit-1", 5)

        // Then
        coVerify { habitDao.updateSortOrder("habit-1", 5) }
    }

    // ==================== Get Active Habit Count Tests ====================

    @Test
    fun `getActiveHabitCount returns count from dao`() = runTest {
        // Given
        every { habitDao.getActiveHabitCount() } returns flowOf(5)

        // When
        val result = repository.getActiveHabitCount().first()

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `getActiveHabitCount returns zero when no habits`() = runTest {
        // Given
        every { habitDao.getActiveHabitCount() } returns flowOf(0)

        // When
        val result = repository.getActiveHabitCount().first()

        // Then
        assertEquals(0, result)
    }

    // ==================== Get Habits With Reminders Tests ====================

    @Test
    fun `getHabitsWithReminders returns habits with reminder time`() = runTest {
        // Given
        coEvery { habitDao.getHabitsWithReminders() } returns listOf(testHabitEntity)

        // When
        val result = repository.getHabitsWithReminders()

        // Then
        assertEquals(1, result.size)
        assertEquals("08:00", result[0].reminderTime)
    }

    @Test
    fun `getHabitsWithReminders returns empty list when no reminders`() = runTest {
        // Given
        coEvery { habitDao.getHabitsWithReminders() } returns emptyList()

        // When
        val result = repository.getHabitsWithReminders()

        // Then
        assertTrue(result.isEmpty())
    }
}
