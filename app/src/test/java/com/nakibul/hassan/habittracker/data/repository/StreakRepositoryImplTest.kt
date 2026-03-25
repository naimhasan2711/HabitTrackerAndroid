package com.nakibul.hassan.habittracker.data.repository

import com.nakibul.hassan.habittracker.data.local.dao.StreakDao
import com.nakibul.hassan.habittracker.data.model.StreakEntity
import com.nakibul.hassan.habittracker.domain.model.Streak
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
class StreakRepositoryImplTest {

    @MockK
    private lateinit var streakDao: StreakDao

    private lateinit var repository: StreakRepositoryImpl

    private val testStreakEntity = StreakEntity(
        id = "streak-1",
        habitId = "habit-1",
        currentStreak = 5,
        longestStreak = 10,
        lastCompletedDate = "2024-01-15"
    )

    private val testStreakEntity2 = StreakEntity(
        id = "streak-2",
        habitId = "habit-2",
        currentStreak = 3,
        longestStreak = 15,
        lastCompletedDate = "2024-01-14"
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = StreakRepositoryImpl(streakDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== Get Streak For Habit Tests ====================

    @Test
    fun `getStreakForHabit returns streak when exists`() = runTest {
        // Given
        coEvery { streakDao.getStreakForHabit("habit-1") } returns testStreakEntity

        // When
        val result = repository.getStreakForHabit("habit-1")

        // Then
        assertNotNull(result)
        assertEquals("habit-1", result?.habitId)
        assertEquals(5, result?.currentStreak)
        assertEquals(10, result?.longestStreak)
    }

    @Test
    fun `getStreakForHabit returns null when not exists`() = runTest {
        // Given
        coEvery { streakDao.getStreakForHabit("non-existent") } returns null

        // When
        val result = repository.getStreakForHabit("non-existent")

        // Then
        assertNull(result)
    }

    // ==================== Get Streak For Habit Flow Tests ====================

    @Test
    fun `getStreakForHabitFlow returns flow with streak`() = runTest {
        // Given
        every { streakDao.getStreakForHabitFlow("habit-1") } returns flowOf(testStreakEntity)

        // When
        val result = repository.getStreakForHabitFlow("habit-1").first()

        // Then
        assertNotNull(result)
        assertEquals(5, result?.currentStreak)
    }

    @Test
    fun `getStreakForHabitFlow returns flow with null when not exists`() = runTest {
        // Given
        every { streakDao.getStreakForHabitFlow("non-existent") } returns flowOf(null)

        // When
        val result = repository.getStreakForHabitFlow("non-existent").first()

        // Then
        assertNull(result)
    }

    // ==================== Get All Streaks Tests ====================

    @Test
    fun `getAllStreaks returns all streaks`() = runTest {
        // Given
        every { streakDao.getAllStreaks() } returns flowOf(listOf(testStreakEntity, testStreakEntity2))

        // When
        val result = repository.getAllStreaks().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("habit-1", result[0].habitId)
        assertEquals("habit-2", result[1].habitId)
    }

    @Test
    fun `getAllStreaks returns empty list when no streaks`() = runTest {
        // Given
        every { streakDao.getAllStreaks() } returns flowOf(emptyList())

        // When
        val result = repository.getAllStreaks().first()

        // Then
        assertTrue(result.isEmpty())
    }

    // ==================== Get Best Streak Tests ====================

    @Test
    fun `getBestStreak returns highest streak value`() = runTest {
        // Given
        every { streakDao.getBestStreak() } returns flowOf(15)

        // When
        val result = repository.getBestStreak().first()

        // Then
        assertEquals(15, result)
    }

    @Test
    fun `getBestStreak returns zero when null`() = runTest {
        // Given
        every { streakDao.getBestStreak() } returns flowOf(null)

        // When
        val result = repository.getBestStreak().first()

        // Then
        assertEquals(0, result)
    }

    // ==================== Insert Streak Tests ====================

    @Test
    fun `insertStreak calls dao insert`() = runTest {
        // Given
        val streak = Streak(
            id = "new-streak",
            habitId = "new-habit",
            currentStreak = 1,
            longestStreak = 1,
            lastCompletedDate = "2024-01-15"
        )
        coEvery { streakDao.insertStreak(any()) } just Runs

        // When
        repository.insertStreak(streak)

        // Then
        coVerify { streakDao.insertStreak(match { it.habitId == "new-habit" }) }
    }

    // ==================== Update Streak Tests ====================

    @Test
    fun `updateStreak calls dao update`() = runTest {
        // Given
        val streak = Streak(
            id = "streak-1",
            habitId = "habit-1",
            currentStreak = 6,
            longestStreak = 10,
            lastCompletedDate = "2024-01-16"
        )
        coEvery { streakDao.updateStreak(any()) } just Runs

        // When
        repository.updateStreak(streak)

        // Then
        coVerify { streakDao.updateStreak(match { it.currentStreak == 6 }) }
    }

    // ==================== Update Streak Values Tests ====================

    @Test
    fun `updateStreakValues calls dao with correct values`() = runTest {
        // Given
        coEvery { streakDao.updateStreakValues(any(), any(), any(), any()) } just Runs

        // When
        repository.updateStreakValues("habit-1", 7, 12, "2024-01-17")

        // Then
        coVerify { streakDao.updateStreakValues("habit-1", 7, 12, "2024-01-17") }
    }

    @Test
    fun `updateStreakValues can set null lastCompletedDate`() = runTest {
        // Given
        coEvery { streakDao.updateStreakValues(any(), any(), any(), any()) } just Runs

        // When
        repository.updateStreakValues("habit-1", 0, 10, null)

        // Then
        coVerify { streakDao.updateStreakValues("habit-1", 0, 10, null) }
    }

    // ==================== Update Freeze Used Tests ====================

    @Test
    fun `updateFreezeUsed sets freeze to true`() = runTest {
        // Given
        coEvery { streakDao.updateFreezeUsed("habit-1", true) } just Runs

        // When
        repository.updateFreezeUsed("habit-1", true)

        // Then
        coVerify { streakDao.updateFreezeUsed("habit-1", true) }
    }

    @Test
    fun `updateFreezeUsed sets freeze to false`() = runTest {
        // Given
        coEvery { streakDao.updateFreezeUsed("habit-1", false) } just Runs

        // When
        repository.updateFreezeUsed("habit-1", false)

        // Then
        coVerify { streakDao.updateFreezeUsed("habit-1", false) }
    }

    // ==================== Delete Streak Tests ====================

    @Test
    fun `deleteStreakForHabit calls dao delete`() = runTest {
        // Given
        coEvery { streakDao.deleteStreakForHabit("habit-1") } just Runs

        // When
        repository.deleteStreakForHabit("habit-1")

        // Then
        coVerify { streakDao.deleteStreakForHabit("habit-1") }
    }
}
