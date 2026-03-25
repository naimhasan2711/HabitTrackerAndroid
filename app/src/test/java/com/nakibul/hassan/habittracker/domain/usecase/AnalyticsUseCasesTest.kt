package com.nakibul.hassan.habittracker.domain.usecase

import app.cash.turbine.test
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.repository.HabitLogRepository
import com.nakibul.hassan.habittracker.domain.repository.HabitRepository
import com.nakibul.hassan.habittracker.domain.repository.StreakRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsUseCasesTest {

    @MockK
    private lateinit var habitRepository: HabitRepository

    @MockK
    private lateinit var habitLogRepository: HabitLogRepository

    @MockK
    private lateinit var streakRepository: StreakRepository

    private lateinit var getAnalyticsUseCase: GetAnalyticsUseCase

    private val testHabit1 = Habit(
        id = "habit-1",
        name = "Exercise",
        description = "Daily workout",
        icon = "fitness",
        color = 0xFF6200EE.toInt(),
        frequency = "daily",
        createdAt = "2024-01-01T10:00:00"
    )

    private val testHabit2 = Habit(
        id = "habit-2",
        name = "Read",
        description = "Read books",
        icon = "book",
        color = 0xFF03DAC5.toInt(),
        frequency = "daily",
        createdAt = "2024-01-01T10:00:00"
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getAnalyticsUseCase = GetAnalyticsUseCase(habitRepository, habitLogRepository, streakRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAnalytics returns correct data for period`() = runTest {
        // Given
        val habits = listOf(testHabit1, testHabit2)
        val logs = listOf(
            HabitLog("log-1", "habit-1", "2024-01-15", true, "2024-01-15T10:00:00"),
            HabitLog("log-2", "habit-2", "2024-01-15", true, "2024-01-15T11:00:00"),
            HabitLog("log-3", "habit-1", "2024-01-14", true, "2024-01-14T10:00:00"),
            HabitLog("log-4", "habit-2", "2024-01-14", false, null)
        )

        every { habitRepository.getAllActiveHabits() } returns flowOf(habits)
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(logs)
        every { streakRepository.getBestStreak() } returns flowOf(5)

        // When & Then
        getAnalyticsUseCase(7).test {
            val result = awaitItem()
            
            assertEquals(7, result.period)
            assertEquals(2, result.activeHabitsCount)
            assertEquals(5, result.bestStreak)
            assertTrue(result.habitCompletions.isNotEmpty())
            
            awaitComplete()
        }
    }

    @Test
    fun `getAnalytics returns zero completion rate when no habits`() = runTest {
        // Given
        every { habitRepository.getAllActiveHabits() } returns flowOf(emptyList())
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(emptyList())
        every { streakRepository.getBestStreak() } returns flowOf(0)

        // When & Then
        getAnalyticsUseCase(7).test {
            val result = awaitItem()
            
            assertEquals(0f, result.overallCompletionRate, 0.001f)
            assertEquals(0, result.activeHabitsCount)
            assertTrue(result.habitCompletions.isEmpty())
            
            awaitComplete()
        }
    }

    @Test
    fun `getAnalytics calculates correct completion rate`() = runTest {
        // Given - 1 habit, 7 days period, 3 completed
        val habits = listOf(testHabit1)
        val logs = listOf(
            HabitLog("log-1", "habit-1", "2024-01-15", true, "2024-01-15T10:00:00"),
            HabitLog("log-2", "habit-1", "2024-01-14", true, "2024-01-14T10:00:00"),
            HabitLog("log-3", "habit-1", "2024-01-13", true, "2024-01-13T10:00:00"),
            HabitLog("log-4", "habit-1", "2024-01-12", false, null)
        )

        every { habitRepository.getAllActiveHabits() } returns flowOf(habits)
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(logs)
        every { streakRepository.getBestStreak() } returns flowOf(3)

        // When & Then
        getAnalyticsUseCase(7).test {
            val result = awaitItem()
            
            // 3 completed out of 7 possible (1 habit * 7 days)
            val expectedRate = 3f / 7f
            assertEquals(expectedRate, result.overallCompletionRate, 0.001f)
            
            awaitComplete()
        }
    }

    @Test
    fun `getAnalytics sorts habits by completion rate descending`() = runTest {
        // Given
        val habits = listOf(testHabit1, testHabit2)
        val logs = listOf(
            // habit-2 has 100% completion (2/2)
            HabitLog("log-1", "habit-2", "2024-01-15", true, "2024-01-15T10:00:00"),
            HabitLog("log-2", "habit-2", "2024-01-14", true, "2024-01-14T11:00:00"),
            // habit-1 has 50% completion (1/2)
            HabitLog("log-3", "habit-1", "2024-01-15", true, "2024-01-15T10:00:00"),
            HabitLog("log-4", "habit-1", "2024-01-14", false, null)
        )

        every { habitRepository.getAllActiveHabits() } returns flowOf(habits)
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(logs)
        every { streakRepository.getBestStreak() } returns flowOf(2)

        // When & Then
        getAnalyticsUseCase(2).test {
            val result = awaitItem()
            
            assertEquals(2, result.habitCompletions.size)
            // First should be habit-2 with higher completion rate
            assertEquals("habit-2", result.habitCompletions[0].habitId)
            assertEquals("habit-1", result.habitCompletions[1].habitId)
            
            awaitComplete()
        }
    }

    @Test
    fun `getAnalytics calculates daily completions correctly`() = runTest {
        // Given
        val habits = listOf(testHabit1, testHabit2)
        val logs = listOf(
            HabitLog("log-1", "habit-1", "2024-01-15", true, "2024-01-15T10:00:00"),
            HabitLog("log-2", "habit-2", "2024-01-15", true, "2024-01-15T11:00:00")
        )

        every { habitRepository.getAllActiveHabits() } returns flowOf(habits)
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(logs)
        every { streakRepository.getBestStreak() } returns flowOf(1)

        // When & Then
        getAnalyticsUseCase(7).test {
            val result = awaitItem()
            
            assertFalse(result.dailyCompletions.isEmpty())
            
            // Find the day with completions
            val dayWith100Percent = result.dailyCompletions.find { 
                it.date == "2024-01-15" 
            }
            
            if (dayWith100Percent != null) {
                assertEquals(2, dayWith100Percent.completedHabits)
                assertEquals(2, dayWith100Percent.totalHabits)
                assertEquals(1f, dayWith100Percent.completionPercentage, 0.001f)
            }
            
            awaitComplete()
        }
    }
}
