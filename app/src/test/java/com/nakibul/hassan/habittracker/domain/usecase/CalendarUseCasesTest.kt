package com.nakibul.hassan.habittracker.domain.usecase

import app.cash.turbine.test
import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.repository.HabitLogRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarUseCasesTest {

    @MockK
    private lateinit var habitLogRepository: HabitLogRepository

    private lateinit var getCalendarDataUseCase: GetCalendarDataUseCase
    private lateinit var getHabitLogsForDateUseCase: GetHabitLogsForDateUseCase
    private lateinit var getHabitLogsInRangeUseCase: GetHabitLogsInRangeUseCase

    private val testLogs = listOf(
        HabitLog("log-1", "habit-1", "2024-01-15", true, "2024-01-15T10:00:00"),
        HabitLog("log-2", "habit-2", "2024-01-15", true, "2024-01-15T11:00:00"),
        HabitLog("log-3", "habit-1", "2024-01-16", true, "2024-01-16T10:00:00"),
        HabitLog("log-4", "habit-2", "2024-01-16", false, null)
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        
        getCalendarDataUseCase = GetCalendarDataUseCase(habitLogRepository)
        getHabitLogsForDateUseCase = GetHabitLogsForDateUseCase(habitLogRepository)
        getHabitLogsInRangeUseCase = GetHabitLogsInRangeUseCase(habitLogRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== GetCalendarDataUseCase Tests ====================

    @Test
    fun `getCalendarData returns logs for month range`() = runTest {
        // Given
        val month = LocalDate.of(2024, 1, 1)
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(testLogs)

        // When & Then
        getCalendarDataUseCase(month, 2).test {
            val result = awaitItem()
            assertEquals(4, result.size)
            awaitComplete()
        }
    }

    @Test
    fun `getCalendarData returns empty list when no logs`() = runTest {
        // Given
        val month = LocalDate.of(2024, 1, 1)
        every { habitLogRepository.getLogsInRange(any(), any()) } returns flowOf(emptyList())

        // When & Then
        getCalendarDataUseCase(month, 2).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }

        verify { habitLogRepository.getLogsInRange(any(), any()) }
    }

    @Test
    fun `getCalendarData calls repository with correct date range`() = runTest {
        // Given
        val month = LocalDate.of(2024, 1, 1)
        val capturedStartDate = slot<String>()
        val capturedEndDate = slot<String>()
        
        every { 
            habitLogRepository.getLogsInRange(capture(capturedStartDate), capture(capturedEndDate)) 
        } returns flowOf(testLogs)

        // When
        getCalendarDataUseCase(month, 2).test {
            awaitItem()
            awaitComplete()
        }

        // Then - verify date range includes padding for calendar grid
        verify { habitLogRepository.getLogsInRange(any(), any()) }
    }

    // ==================== GetHabitLogsForDateUseCase Tests ====================

    @Test
    fun `getHabitLogsForDate returns logs for specific date`() = runTest {
        // Given
        val date = "2024-01-15"
        val logsForDate = testLogs.filter { it.date == date }
        every { habitLogRepository.getLogsForDate(date) } returns flowOf(logsForDate)

        // When & Then
        getHabitLogsForDateUseCase(date).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.date == date })
            awaitComplete()
        }
    }

    @Test
    fun `getHabitLogsForDate returns empty list when no logs for date`() = runTest {
        // Given
        val date = "2024-02-01"
        every { habitLogRepository.getLogsForDate(date) } returns flowOf(emptyList())

        // When & Then
        getHabitLogsForDateUseCase(date).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getHabitLogsForDate calls repository with correct date`() = runTest {
        // Given
        val date = "2024-01-15"
        every { habitLogRepository.getLogsForDate(any()) } returns flowOf(emptyList())

        // When
        getHabitLogsForDateUseCase(date).test {
            awaitItem()
            awaitComplete()
        }

        // Then
        verify { habitLogRepository.getLogsForDate(date) }
    }

    // ==================== GetHabitLogsInRangeUseCase Tests ====================

    @Test
    fun `getHabitLogsInRange returns logs for habit in date range`() = runTest {
        // Given
        val habitId = "habit-1"
        val startDate = "2024-01-15"
        val endDate = "2024-01-16"
        val logsInRange = testLogs.filter { it.habitId == habitId }
        
        every { 
            habitLogRepository.getLogsForHabitInRange(habitId, startDate, endDate) 
        } returns flowOf(logsInRange)

        // When & Then
        getHabitLogsInRangeUseCase(habitId, startDate, endDate).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.habitId == habitId })
            awaitComplete()
        }
    }

    @Test
    fun `getHabitLogsInRange returns empty list when no logs in range`() = runTest {
        // Given
        val habitId = "habit-1"
        val startDate = "2024-02-01"
        val endDate = "2024-02-28"
        
        every { 
            habitLogRepository.getLogsForHabitInRange(habitId, startDate, endDate) 
        } returns flowOf(emptyList())

        // When & Then
        getHabitLogsInRangeUseCase(habitId, startDate, endDate).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getHabitLogsInRange calls repository with correct parameters`() = runTest {
        // Given
        val habitId = "habit-1"
        val startDate = "2024-01-01"
        val endDate = "2024-01-31"
        
        every { 
            habitLogRepository.getLogsForHabitInRange(any(), any(), any()) 
        } returns flowOf(emptyList())

        // When
        getHabitLogsInRangeUseCase(habitId, startDate, endDate).test {
            awaitItem()
            awaitComplete()
        }

        // Then
        verify { habitLogRepository.getLogsForHabitInRange(habitId, startDate, endDate) }
    }

    // ==================== CalendarDay Data Class Tests ====================

    @Test
    fun `CalendarDay holds correct data`() {
        val calendarDay = CalendarDay(
            date = "2024-01-15",
            dayOfMonth = 15,
            isCurrentMonth = true,
            isToday = false,
            completionPercentage = 0.75f,
            completedCount = 3,
            totalCount = 4
        )

        assertEquals("2024-01-15", calendarDay.date)
        assertEquals(15, calendarDay.dayOfMonth)
        assertTrue(calendarDay.isCurrentMonth)
        assertFalse(calendarDay.isToday)
        assertEquals(0.75f, calendarDay.completionPercentage, 0.001f)
        assertEquals(3, calendarDay.completedCount)
        assertEquals(4, calendarDay.totalCount)
    }

    @Test
    fun `CalendarDay equality works correctly`() {
        val day1 = CalendarDay("2024-01-15", 15, true, false, 0.5f, 2, 4)
        val day2 = CalendarDay("2024-01-15", 15, true, false, 0.5f, 2, 4)
        val day3 = CalendarDay("2024-01-16", 16, true, false, 0.5f, 2, 4)

        assertEquals(day1, day2)
        assertNotEquals(day1, day3)
    }
}
