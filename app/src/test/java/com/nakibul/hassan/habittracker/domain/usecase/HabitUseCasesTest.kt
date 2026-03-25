package com.nakibul.hassan.habittracker.domain.usecase

import app.cash.turbine.test
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.model.Streak
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
class HabitUseCasesTest {

    @MockK
    private lateinit var habitRepository: HabitRepository

    @MockK
    private lateinit var habitLogRepository: HabitLogRepository

    @MockK
    private lateinit var streakRepository: StreakRepository

    private lateinit var toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase
    private lateinit var getActiveHabitsUseCase: GetActiveHabitsUseCase
    private lateinit var getHabitByIdUseCase: GetHabitByIdUseCase
    private lateinit var createHabitUseCase: CreateHabitUseCase
    private lateinit var updateHabitUseCase: UpdateHabitUseCase
    private lateinit var deleteHabitUseCase: DeleteHabitUseCase
    private lateinit var archiveHabitUseCase: ArchiveHabitUseCase

    private val testHabit = Habit(
        id = "habit-1",
        name = "Exercise",
        description = "Daily workout",
        icon = "fitness",
        color = 0xFF6200EE.toInt(),
        frequency = "daily",
        createdAt = "2024-01-01T10:00:00"
    )

    private val testHabitLog = HabitLog(
        id = "log-1",
        habitId = "habit-1",
        date = "2024-01-15",
        completed = false,
        completedAt = null
    )

    private val testStreak = Streak(
        id = "streak-1",
        habitId = "habit-1",
        currentStreak = 5,
        longestStreak = 10,
        lastCompletedDate = "2024-01-14"
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        
        toggleHabitCompletionUseCase = ToggleHabitCompletionUseCase(habitLogRepository, streakRepository)
        getActiveHabitsUseCase = GetActiveHabitsUseCase(habitRepository)
        getHabitByIdUseCase = GetHabitByIdUseCase(habitRepository)
        createHabitUseCase = CreateHabitUseCase(habitRepository, streakRepository)
        updateHabitUseCase = UpdateHabitUseCase(habitRepository)
        deleteHabitUseCase = DeleteHabitUseCase(habitRepository)
        archiveHabitUseCase = ArchiveHabitUseCase(habitRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== GetActiveHabitsUseCase Tests ====================

    @Test
    fun `getActiveHabits returns flow of active habits`() = runTest {
        // Given
        val habits = listOf(testHabit, testHabit.copy(id = "habit-2", name = "Read"))
        every { habitRepository.getAllActiveHabits() } returns flowOf(habits)

        // When & Then
        getActiveHabitsUseCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Exercise", result[0].name)
            assertEquals("Read", result[1].name)
            awaitComplete()
        }

        verify { habitRepository.getAllActiveHabits() }
    }

    @Test
    fun `getActiveHabits returns empty list when no habits`() = runTest {
        // Given
        every { habitRepository.getAllActiveHabits() } returns flowOf(emptyList())

        // When & Then
        getActiveHabitsUseCase().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    // ==================== GetHabitByIdUseCase Tests ====================

    @Test
    fun `getHabitById returns habit when exists`() = runTest {
        // Given
        every { habitRepository.getHabitById("habit-1") } returns flowOf(testHabit)

        // When & Then
        getHabitByIdUseCase("habit-1").test {
            val result = awaitItem()
            assertNotNull(result)
            assertEquals("habit-1", result?.id)
            assertEquals("Exercise", result?.name)
            awaitComplete()
        }
    }

    @Test
    fun `getHabitById returns null when habit not found`() = runTest {
        // Given
        every { habitRepository.getHabitById("non-existent") } returns flowOf(null)

        // When & Then
        getHabitByIdUseCase("non-existent").test {
            val result = awaitItem()
            assertNull(result)
            awaitComplete()
        }
    }

    // ==================== CreateHabitUseCase Tests ====================

    @Test
    fun `createHabit inserts habit and creates streak`() = runTest {
        // Given
        coEvery { habitRepository.insertHabit(any()) } just Runs
        coEvery { streakRepository.insertStreak(any()) } just Runs

        // When
        createHabitUseCase(testHabit)

        // Then
        coVerify { habitRepository.insertHabit(testHabit) }
        coVerify { 
            streakRepository.insertStreak(match { streak ->
                streak.habitId == testHabit.id &&
                streak.currentStreak == 0 &&
                streak.longestStreak == 0
            })
        }
    }

    @Test
    fun `createHabit creates streak with zero values`() = runTest {
        // Given
        val capturedStreak = slot<Streak>()
        coEvery { habitRepository.insertHabit(any()) } just Runs
        coEvery { streakRepository.insertStreak(capture(capturedStreak)) } just Runs

        // When
        createHabitUseCase(testHabit)

        // Then
        assertEquals(0, capturedStreak.captured.currentStreak)
        assertEquals(0, capturedStreak.captured.longestStreak)
        assertNull(capturedStreak.captured.lastCompletedDate)
    }

    // ==================== UpdateHabitUseCase Tests ====================

    @Test
    fun `updateHabit calls repository update`() = runTest {
        // Given
        val updatedHabit = testHabit.copy(name = "Updated Exercise")
        coEvery { habitRepository.updateHabit(any()) } just Runs

        // When
        updateHabitUseCase(updatedHabit)

        // Then
        coVerify { habitRepository.updateHabit(updatedHabit) }
    }

    // ==================== DeleteHabitUseCase Tests ====================

    @Test
    fun `deleteHabit calls repository delete`() = runTest {
        // Given
        coEvery { habitRepository.deleteHabit(any()) } just Runs

        // When
        deleteHabitUseCase("habit-1")

        // Then
        coVerify { habitRepository.deleteHabit("habit-1") }
    }

    // ==================== ArchiveHabitUseCase Tests ====================

    @Test
    fun `archiveHabit sets archived to true`() = runTest {
        // Given
        coEvery { habitRepository.archiveHabit(any(), any()) } just Runs

        // When
        archiveHabitUseCase("habit-1", true)

        // Then
        coVerify { habitRepository.archiveHabit("habit-1", true) }
    }

    @Test
    fun `archiveHabit sets archived to false for restore`() = runTest {
        // Given
        coEvery { habitRepository.archiveHabit(any(), any()) } just Runs

        // When
        archiveHabitUseCase("habit-1", false)

        // Then
        coVerify { habitRepository.archiveHabit("habit-1", false) }
    }

    // ==================== ToggleHabitCompletionUseCase Tests ====================

    @Test
    fun `toggleHabitCompletion creates new log when no existing log`() = runTest {
        // Given
        coEvery { habitLogRepository.getLogForHabitOnDate("habit-1", any()) } returns null
        coEvery { habitLogRepository.insertLog(any()) } just Runs
        coEvery { streakRepository.getStreakForHabit("habit-1") } returns null
        coEvery { streakRepository.insertStreak(any()) } just Runs

        // When
        toggleHabitCompletionUseCase("habit-1", "2024-01-15")

        // Then
        coVerify { 
            habitLogRepository.insertLog(match { log ->
                log.habitId == "habit-1" &&
                log.date == "2024-01-15" &&
                log.completed == true
            })
        }
    }

    @Test
    fun `toggleHabitCompletion toggles existing incomplete log to complete`() = runTest {
        // Given
        coEvery { habitLogRepository.getLogForHabitOnDate("habit-1", "2024-01-15") } returns testHabitLog
        coEvery { habitLogRepository.updateCompletionStatus(any(), any(), any(), any()) } just Runs
        coEvery { streakRepository.getStreakForHabit("habit-1") } returns testStreak
        coEvery { streakRepository.updateStreakValues(any(), any(), any(), any()) } just Runs

        // When
        toggleHabitCompletionUseCase("habit-1", "2024-01-15")

        // Then
        coVerify { 
            habitLogRepository.updateCompletionStatus(
                habitId = "habit-1",
                date = "2024-01-15",
                completed = true,
                completedAt = any()
            )
        }
    }

    @Test
    fun `toggleHabitCompletion toggles existing complete log to incomplete`() = runTest {
        // Given
        val completedLog = testHabitLog.copy(completed = true, completedAt = "2024-01-15T10:00:00")
        coEvery { habitLogRepository.getLogForHabitOnDate("habit-1", "2024-01-15") } returns completedLog
        coEvery { habitLogRepository.updateCompletionStatus(any(), any(), any(), any()) } just Runs
        coEvery { streakRepository.getStreakForHabit("habit-1") } returns testStreak.copy(lastCompletedDate = "2024-01-15")
        coEvery { streakRepository.updateStreakValues(any(), any(), any(), any()) } just Runs

        // When
        toggleHabitCompletionUseCase("habit-1", "2024-01-15")

        // Then
        coVerify { 
            habitLogRepository.updateCompletionStatus(
                habitId = "habit-1",
                date = "2024-01-15",
                completed = false,
                completedAt = null
            )
        }
    }

    @Test
    fun `toggleHabitCompletion creates new streak when none exists`() = runTest {
        // Given
        coEvery { habitLogRepository.getLogForHabitOnDate("habit-1", any()) } returns null
        coEvery { habitLogRepository.insertLog(any()) } just Runs
        coEvery { streakRepository.getStreakForHabit("habit-1") } returns null
        coEvery { streakRepository.insertStreak(any()) } just Runs

        // When
        toggleHabitCompletionUseCase("habit-1", "2024-01-15")

        // Then
        coVerify {
            streakRepository.insertStreak(match { streak ->
                streak.habitId == "habit-1" &&
                streak.currentStreak == 1 &&
                streak.longestStreak == 1 &&
                streak.lastCompletedDate == "2024-01-15"
            })
        }
    }
}
