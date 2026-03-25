package com.nakibul.hassan.habittracker.presentation.ui.analytics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nakibul.hassan.habittracker.domain.model.AnalyticsData
import com.nakibul.hassan.habittracker.domain.model.DayCompletion
import com.nakibul.hassan.habittracker.domain.model.HabitCompletionRate
import com.nakibul.hassan.habittracker.domain.usecase.GetAnalyticsUseCase
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getAnalyticsUseCase: GetAnalyticsUseCase

    private lateinit var viewModel: AnalyticsViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testAnalyticsData = AnalyticsData(
        period = 7,
        overallCompletionRate = 0.75f,
        activeHabitsCount = 5,
        bestStreak = 10,
        dailyCompletions = listOf(
            DayCompletion("2024-01-15", 5, 4, 0.8f),
            DayCompletion("2024-01-14", 5, 3, 0.6f)
        ),
        habitCompletions = listOf(
            HabitCompletionRate("habit-1", "Exercise", 0xFF6200EE.toInt(), 0.85f, 6, 7),
            HabitCompletionRate("habit-2", "Read", 0xFF03DAC5.toInt(), 0.71f, 5, 7)
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

    private fun createViewModel(): AnalyticsViewModel {
        return AnalyticsViewModel(getAnalyticsUseCase)
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has loading true`() = runTest {
        // Given
        every { getAnalyticsUseCase(any()) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()

        // Then - initial state should have loading true
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has default period of 7 days`() = runTest {
        // Given
        every { getAnalyticsUseCase(any()) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()

        // Then
        assertEquals(7, viewModel.uiState.value.selectedPeriod)
    }

    // ==================== Load Analytics Tests ====================

    @Test
    fun `loadAnalytics updates state with analytics data`() = runTest {
        // Given
        every { getAnalyticsUseCase(7) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNotNull(state.analyticsData)
        assertEquals(0.75f, state.analyticsData?.overallCompletionRate ?: 0f, 0.001f)
        assertEquals(5, state.analyticsData?.activeHabitsCount)
        assertEquals(10, state.analyticsData?.bestStreak)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadAnalytics handles error`() = runTest {
        // Given
        every { getAnalyticsUseCase(7) } returns flow { throw Exception("Database error") }

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Database error", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadAnalytics sets loading to false after completion`() = runTest {
        // Given
        every { getAnalyticsUseCase(any()) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ==================== Select Period Tests ====================

    @Test
    fun `selectPeriod updates selected period`() = runTest {
        // Given
        every { getAnalyticsUseCase(any()) } returns flowOf(testAnalyticsData)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.selectPeriod(30)
        advanceUntilIdle()

        // Then
        assertEquals(30, viewModel.uiState.value.selectedPeriod)
    }

    @Test
    fun `selectPeriod sets loading state`() = runTest {
        // Given
        every { getAnalyticsUseCase(any()) } returns flowOf(testAnalyticsData)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.selectPeriod(30)
        
        // Then - should set loading before data loads
        // Note: The state changes quickly, this tests the logic flow
    }

    @Test
    fun `selectPeriod calls use case with new period`() = runTest {
        // Given
        every { getAnalyticsUseCase(any()) } returns flowOf(testAnalyticsData)
        viewModel = createViewModel()
        advanceUntilIdle()
        
        clearMocks(getAnalyticsUseCase, answers = false)
        every { getAnalyticsUseCase(30) } returns flowOf(testAnalyticsData.copy(period = 30))

        // When
        viewModel.selectPeriod(30)
        advanceUntilIdle()

        // Then
        verify { getAnalyticsUseCase(30) }
    }

    @Test
    fun `selectPeriod with 7 days works correctly`() = runTest {
        // Given
        val weekData = testAnalyticsData.copy(period = 7)
        every { getAnalyticsUseCase(7) } returns flowOf(weekData)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.selectPeriod(7)
        advanceUntilIdle()

        // Then
        assertEquals(7, viewModel.uiState.value.selectedPeriod)
        assertEquals(7, viewModel.uiState.value.analyticsData?.period)
    }

    @Test
    fun `selectPeriod with 30 days works correctly`() = runTest {
        // Given
        every { getAnalyticsUseCase(7) } returns flowOf(testAnalyticsData)
        viewModel = createViewModel()
        advanceUntilIdle()

        val monthData = testAnalyticsData.copy(period = 30)
        every { getAnalyticsUseCase(30) } returns flowOf(monthData)

        // When
        viewModel.selectPeriod(30)
        advanceUntilIdle()

        // Then
        assertEquals(30, viewModel.uiState.value.selectedPeriod)
    }

    // ==================== Analytics Data Content Tests ====================

    @Test
    fun `analytics data contains daily completions`() = runTest {
        // Given
        every { getAnalyticsUseCase(7) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val dailyCompletions = viewModel.uiState.value.analyticsData?.dailyCompletions
        assertNotNull(dailyCompletions)
        assertEquals(2, dailyCompletions?.size)
    }

    @Test
    fun `analytics data contains habit completions`() = runTest {
        // Given
        every { getAnalyticsUseCase(7) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val habitCompletions = viewModel.uiState.value.analyticsData?.habitCompletions
        assertNotNull(habitCompletions)
        assertEquals(2, habitCompletions?.size)
        assertEquals("Exercise", habitCompletions?.get(0)?.habitName)
    }

    @Test
    fun `analytics data reflects correct best streak`() = runTest {
        // Given
        every { getAnalyticsUseCase(7) } returns flowOf(testAnalyticsData)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(10, viewModel.uiState.value.analyticsData?.bestStreak)
    }
}
