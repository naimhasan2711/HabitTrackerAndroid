package com.nakibul.hassan.habittracker.presentation.ui.calendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.usecase.GetActiveHabitsUseCase
import com.nakibul.hassan.habittracker.domain.usecase.GetCalendarDataUseCase
import com.nakibul.hassan.habittracker.domain.usecase.GetHabitLogsForDateUseCase
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getActiveHabitsUseCase: GetActiveHabitsUseCase

    @MockK
    private lateinit var getCalendarDataUseCase: GetCalendarDataUseCase

    @MockK
    private lateinit var getHabitLogsForDateUseCase: GetHabitLogsForDateUseCase

    private lateinit var viewModel: CalendarViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testHabits = listOf(
        Habit(
            id = "habit-1",
            name = "Exercise",
            description = "Daily workout",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = "cat-1",
            reminderTime = null,
            frequency = "daily",
            archived = false,
            createdAt = "2024-01-01T10:00:00"
        ),
        Habit(
            id = "habit-2",
            name = "Read",
            description = "Read books",
            icon = "book",
            color = 0xFF03DAC5.toInt(),
            categoryId = "cat-2",
            reminderTime = "21:00",
            frequency = "daily",
            archived = false,
            createdAt = "2024-01-01T10:00:00"
        )
    )

    private val testLogs = listOf(
        HabitLog(
            id = "log-1",
            habitId = "habit-1",
            date = "2024-01-15",
            completed = true,
            completedAt = "2024-01-15T10:00:00"
        ),
        HabitLog(
            id = "log-2",
            habitId = "habit-2",
            date = "2024-01-15",
            completed = true,
            completedAt = "2024-01-15T10:00:00"
        ),
        HabitLog(
            id = "log-3",
            habitId = "habit-1",
            date = "2024-01-14",
            completed = true,
            completedAt = "2024-01-14T10:00:00"
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(): CalendarViewModel {
        return CalendarViewModel(
            getActiveHabitsUseCase,
            getCalendarDataUseCase,
            getHabitLogsForDateUseCase
        )
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has loading true`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has current month set to first day of current month`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()

        // Then
        val expectedFirstDay = LocalDate.now().withDayOfMonth(1)
        assertEquals(expectedFirstDay, viewModel.uiState.value.currentMonth)
    }

    // ==================== Load Data Tests ====================

    @Test
    fun `loadData updates habits`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.uiState.value.habits.size)
        assertEquals("Exercise", viewModel.uiState.value.habits[0].name)
    }

    @Test
    fun `loadData updates logs for month`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(3, viewModel.uiState.value.logsForMonth.size)
    }

    @Test
    fun `loadData calculates completion by date`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val completionByDate = viewModel.uiState.value.completionByDate
        // 2024-01-15: 2 completed out of 2 habits = 1.0f
        assertEquals(1.0f, completionByDate["2024-01-15"] ?: 0f, 0.001f)
        // 2024-01-14: 1 completed out of 2 habits = 0.5f
        assertEquals(0.5f, completionByDate["2024-01-14"] ?: 0f, 0.001f)
    }

    @Test
    fun `loadData sets loading to false`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadData with empty habits calculates zero completion`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(emptyList())
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(emptyList())
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.completionByDate.isEmpty())
    }

    // ==================== Select Date Tests ====================

    @Test
    fun `selectDate updates selected date`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.selectDate("2024-01-20")
        advanceUntilIdle()

        // Then
        assertEquals("2024-01-20", viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `selectDate loads logs for selected date`() = runTest {
        // Given
        val logsForDate = listOf(testLogs[0], testLogs[1])
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        every { getHabitLogsForDateUseCase("2024-01-15") } returns flowOf(logsForDate)

        // When
        viewModel.selectDate("2024-01-15")
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.uiState.value.logsForSelectedDate.size)
    }

    // ==================== Navigation Tests ====================

    @Test
    fun `navigateToPreviousMonth decreases month by one`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialMonth = viewModel.uiState.value.currentMonth

        // When
        viewModel.navigateToPreviousMonth()
        advanceUntilIdle()

        // Then
        assertEquals(initialMonth.minusMonths(1), viewModel.uiState.value.currentMonth)
    }

    @Test
    fun `navigateToNextMonth increases month by one`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialMonth = viewModel.uiState.value.currentMonth

        // When
        viewModel.navigateToNextMonth()
        advanceUntilIdle()

        // Then
        assertEquals(initialMonth.plusMonths(1), viewModel.uiState.value.currentMonth)
    }

    @Test
    fun `navigateToPreviousMonth reloads data`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.navigateToPreviousMonth()
        advanceUntilIdle()

        // Then - verify data loading occurred (calendar data was fetched again)
        verify(atLeast = 2) { getCalendarDataUseCase(any(), any()) }
    }

    @Test
    fun `navigateToNextMonth reloads data`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(testHabits)
        every { getCalendarDataUseCase(any(), any()) } returns flowOf(testLogs)
        every { getHabitLogsForDateUseCase(any()) } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.navigateToNextMonth()
        advanceUntilIdle()

        // Then
        verify(atLeast = 2) { getCalendarDataUseCase(any(), any()) }
    }
}
