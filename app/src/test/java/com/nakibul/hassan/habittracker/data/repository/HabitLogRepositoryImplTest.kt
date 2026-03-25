package com.nakibul.hassan.habittracker.data.repository

import com.nakibul.hassan.habittracker.data.local.dao.HabitLogDao
import com.nakibul.hassan.habittracker.data.model.HabitLogEntity
import com.nakibul.hassan.habittracker.domain.model.HabitLog
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
class HabitLogRepositoryImplTest {

    @MockK
    private lateinit var habitLogDao: HabitLogDao

    private lateinit var repository: HabitLogRepositoryImpl

    private val testLogEntity = HabitLogEntity(
        id = "log-1",
        habitId = "habit-1",
        date = "2024-01-15",
        completed = true,
        completedAt = "2024-01-15T10:00:00"
    )

    private val testLogEntity2 = HabitLogEntity(
        id = "log-2",
        habitId = "habit-1",
        date = "2024-01-14",
        completed = true,
        completedAt = "2024-01-14T10:00:00"
    )

    private val testLogEntity3 = HabitLogEntity(
        id = "log-3",
        habitId = "habit-2",
        date = "2024-01-15",
        completed = false,
        completedAt = null
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = HabitLogRepositoryImpl(habitLogDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== Get Logs For Habit Tests ====================

    @Test
    fun `getLogsForHabit returns logs for specific habit`() = runTest {
        // Given
        every { habitLogDao.getLogsForHabit("habit-1") } returns flowOf(listOf(testLogEntity, testLogEntity2))

        // When
        val result = repository.getLogsForHabit("habit-1").first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.habitId == "habit-1" })
    }

    @Test
    fun `getLogsForHabit returns empty list when no logs`() = runTest {
        // Given
        every { habitLogDao.getLogsForHabit("habit-1") } returns flowOf(emptyList())

        // When
        val result = repository.getLogsForHabit("habit-1").first()

        // Then
        assertTrue(result.isEmpty())
    }

    // ==================== Get Log For Habit On Date Tests ====================

    @Test
    fun `getLogForHabitOnDate returns log when exists`() = runTest {
        // Given
        coEvery { habitLogDao.getLogForHabitOnDate("habit-1", "2024-01-15") } returns testLogEntity

        // When
        val result = repository.getLogForHabitOnDate("habit-1", "2024-01-15")

        // Then
        assertNotNull(result)
        assertEquals("log-1", result?.id)
        assertTrue(result?.completed == true)
    }

    @Test
    fun `getLogForHabitOnDate returns null when not exists`() = runTest {
        // Given
        coEvery { habitLogDao.getLogForHabitOnDate("habit-1", "2024-01-20") } returns null

        // When
        val result = repository.getLogForHabitOnDate("habit-1", "2024-01-20")

        // Then
        assertNull(result)
    }

    // ==================== Get Log For Habit On Date Flow Tests ====================

    @Test
    fun `getLogForHabitOnDateFlow returns flow with log`() = runTest {
        // Given
        every { habitLogDao.getLogForHabitOnDateFlow("habit-1", "2024-01-15") } returns flowOf(testLogEntity)

        // When
        val result = repository.getLogForHabitOnDateFlow("habit-1", "2024-01-15").first()

        // Then
        assertNotNull(result)
        assertEquals("log-1", result?.id)
    }

    @Test
    fun `getLogForHabitOnDateFlow returns flow with null when not exists`() = runTest {
        // Given
        every { habitLogDao.getLogForHabitOnDateFlow("habit-1", "2024-01-20") } returns flowOf(null)

        // When
        val result = repository.getLogForHabitOnDateFlow("habit-1", "2024-01-20").first()

        // Then
        assertNull(result)
    }

    // ==================== Get Logs For Date Tests ====================

    @Test
    fun `getLogsForDate returns all logs for specific date`() = runTest {
        // Given
        every { habitLogDao.getLogsForDate("2024-01-15") } returns flowOf(listOf(testLogEntity, testLogEntity3))

        // When
        val result = repository.getLogsForDate("2024-01-15").first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.date == "2024-01-15" })
    }

    // ==================== Get Logs For Habit In Range Tests ====================

    @Test
    fun `getLogsForHabitInRange returns logs within date range`() = runTest {
        // Given
        every { habitLogDao.getLogsForHabitInRange("habit-1", "2024-01-14", "2024-01-15") } returns 
            flowOf(listOf(testLogEntity, testLogEntity2))

        // When
        val result = repository.getLogsForHabitInRange("habit-1", "2024-01-14", "2024-01-15").first()

        // Then
        assertEquals(2, result.size)
    }

    // ==================== Get Logs In Range Tests ====================

    @Test
    fun `getLogsInRange returns all logs within date range`() = runTest {
        // Given
        every { habitLogDao.getLogsInRange("2024-01-14", "2024-01-15") } returns 
            flowOf(listOf(testLogEntity, testLogEntity2, testLogEntity3))

        // When
        val result = repository.getLogsInRange("2024-01-14", "2024-01-15").first()

        // Then
        assertEquals(3, result.size)
    }

    // ==================== Get Completion Count Tests ====================

    @Test
    fun `getCompletionCountForHabitInRange returns correct count`() = runTest {
        // Given
        coEvery { habitLogDao.getCompletionCountForHabitInRange("habit-1", "2024-01-01", "2024-01-15") } returns 10

        // When
        val result = repository.getCompletionCountForHabitInRange("habit-1", "2024-01-01", "2024-01-15")

        // Then
        assertEquals(10, result)
    }

    @Test
    fun `getCompletionCountForDate returns count from flow`() = runTest {
        // Given
        every { habitLogDao.getCompletionCountForDate("2024-01-15") } returns flowOf(5)

        // When
        val result = repository.getCompletionCountForDate("2024-01-15").first()

        // Then
        assertEquals(5, result)
    }

    // ==================== Insert Log Tests ====================

    @Test
    fun `insertLog calls dao insert`() = runTest {
        // Given
        val log = HabitLog(
            id = "new-log",
            habitId = "habit-1",
            date = "2024-01-16",
            completed = true,
            completedAt = "2024-01-16T10:00:00"
        )
        coEvery { habitLogDao.insertLog(any()) } just Runs

        // When
        repository.insertLog(log)

        // Then
        coVerify { habitLogDao.insertLog(match { it.id == "new-log" }) }
    }

    // ==================== Update Completion Status Tests ====================

    @Test
    fun `updateCompletionStatus calls dao update`() = runTest {
        // Given
        coEvery { habitLogDao.updateCompletionStatus(any(), any(), any(), any()) } just Runs

        // When
        repository.updateCompletionStatus("habit-1", "2024-01-15", true, "1705300000000")

        // Then
        coVerify { habitLogDao.updateCompletionStatus("habit-1", "2024-01-15", true, "1705300000000") }
    }

    @Test
    fun `updateCompletionStatus can set to incomplete`() = runTest {
        // Given
        coEvery { habitLogDao.updateCompletionStatus(any(), any(), any(), any()) } just Runs

        // When
        repository.updateCompletionStatus("habit-1", "2024-01-15", false, null)

        // Then
        coVerify { habitLogDao.updateCompletionStatus("habit-1", "2024-01-15", false, null) }
    }

    // ==================== Delete Logs Tests ====================

    @Test
    fun `deleteLogsForHabit calls dao delete`() = runTest {
        // Given
        coEvery { habitLogDao.deleteLogsForHabit("habit-1") } just Runs

        // When
        repository.deleteLogsForHabit("habit-1")

        // Then
        coVerify { habitLogDao.deleteLogsForHabit("habit-1") }
    }

    // ==================== Get Completed Dates In Range Tests ====================

    @Test
    fun `getCompletedDatesInRange returns list of dates`() = runTest {
        // Given
        coEvery { habitLogDao.getCompletedDatesInRange("2024-01-01", "2024-01-15") } returns 
            listOf("2024-01-05", "2024-01-10", "2024-01-15")

        // When
        val result = repository.getCompletedDatesInRange("2024-01-01", "2024-01-15")

        // Then
        assertEquals(3, result.size)
        assertTrue(result.contains("2024-01-10"))
    }

    @Test
    fun `getCompletedDatesInRange returns empty list when no completions`() = runTest {
        // Given
        coEvery { habitLogDao.getCompletedDatesInRange("2024-01-01", "2024-01-15") } returns emptyList()

        // When
        val result = repository.getCompletedDatesInRange("2024-01-01", "2024-01-15")

        // Then
        assertTrue(result.isEmpty())
    }
}
