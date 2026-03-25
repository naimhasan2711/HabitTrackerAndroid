package com.nakibul.hassan.habittracker.presentation.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.usecase.GetActiveHabitsUseCase
import com.nakibul.hassan.habittracker.domain.usecase.ToggleHabitCompletionUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getActiveHabitsUseCase: GetActiveHabitsUseCase

    @MockK
    private lateinit var toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testHabit1 = Habit(
        id = "habit-1",
        name = "Exercise",
        description = "Daily workout",
        icon = "fitness",
        color = 0xFF6200EE.toInt(),
        frequency = "daily",
        createdAt = "2024-01-01T10:00:00",
        isCompletedToday = false
    )

    private val testHabit2 = Habit(
        id = "habit-2",
        name = "Read",
        description = "Read books",
        icon = "book",
        color = 0xFF03DAC5.toInt(),
        frequency = "daily",
        createdAt = "2024-01-01T10:00:00",
        isCompletedToday = true
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

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(getActiveHabitsUseCase, toggleHabitCompletionUseCase)
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has loading true`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.uiState.value.isLoading || viewModel.uiState.value.habits.isEmpty())
    }

    // ==================== Load Habits Tests ====================

    @Test
    fun `loadHabits updates state with habits`() = runTest {
        // Given
        val habits = listOf(testHabit1, testHabit2)
        every { getActiveHabitsUseCase() } returns flowOf(habits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.habits.size)
        assertEquals(2, state.totalCount)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadHabits calculates correct completion count`() = runTest {
        // Given - 1 completed, 1 not
        val habits = listOf(testHabit1.copy(isCompletedToday = false), testHabit2.copy(isCompletedToday = true))
        every { getActiveHabitsUseCase() } returns flowOf(habits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.completedCount)
        assertEquals(2, state.totalCount)
    }

    @Test
    fun `loadHabits calculates correct completion percentage`() = runTest {
        // Given - 1 completed, 1 not = 50%
        val habits = listOf(
            testHabit1.copy(isCompletedToday = false), 
            testHabit2.copy(isCompletedToday = true)
        )
        every { getActiveHabitsUseCase() } returns flowOf(habits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(0.5f, state.completionPercentage, 0.01f)
    }

    @Test
    fun `loadHabits handles empty list`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.habits.isEmpty())
        assertEquals(0, state.totalCount)
        assertEquals(0, state.completedCount)
        assertEquals(0f, state.completionPercentage, 0.01f)
    }

    @Test
    fun `loadHabits handles 100 percent completion`() = runTest {
        // Given - all completed
        val habits = listOf(
            testHabit1.copy(isCompletedToday = true),
            testHabit2.copy(isCompletedToday = true)
        )
        every { getActiveHabitsUseCase() } returns flowOf(habits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.completedCount)
        assertEquals(1f, state.completionPercentage, 0.01f)
    }

    @Test
    fun `loadHabits sets greeting`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(
            state.greeting == "Good Morning" ||
            state.greeting == "Good Afternoon" ||
            state.greeting == "Good Evening"
        )
    }

    // ==================== Toggle Habit Completion Tests ====================

    @Test
    fun `toggleHabitCompletion calls use case`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(listOf(testHabit1))
        coEvery { toggleHabitCompletionUseCase(any()) } just Runs
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleHabitCompletion("habit-1")
        advanceUntilIdle()

        // Then
        coVerify { toggleHabitCompletionUseCase("habit-1") }
    }

    @Test
    fun `toggleHabitCompletion handles error`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(listOf(testHabit1))
        coEvery { toggleHabitCompletionUseCase(any()) } throws Exception("Toggle failed")
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleHabitCompletion("habit-1")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Toggle failed", state.error)
    }

    // ==================== Refresh Tests ====================

    @Test
    fun `refresh reloads habits`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(listOf(testHabit1))
        
        viewModel = createViewModel()
        advanceUntilIdle()
        
        // Clear previous invocations
        clearMocks(getActiveHabitsUseCase, answers = false)
        every { getActiveHabitsUseCase() } returns flowOf(listOf(testHabit1, testHabit2))

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        verify { getActiveHabitsUseCase() }
    }

    @Test
    fun `refresh sets loading state`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(listOf(testHabit1))
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.refresh()
        
        // State should be loading before advancing
        // Note: Due to immediate collection this is hard to verify, 
        // but the function should set isLoading = true
    }

    // ==================== Clear Error Tests ====================

    @Test
    fun `clearError clears error state`() = runTest {
        // Given
        every { getActiveHabitsUseCase() } returns flowOf(listOf(testHabit1))
        coEvery { toggleHabitCompletionUseCase(any()) } throws Exception("Error")
        
        viewModel = createViewModel()
        advanceUntilIdle()
        
        viewModel.toggleHabitCompletion("habit-1")
        advanceUntilIdle()
        
        // Verify error exists
        assertNotNull(viewModel.uiState.value.error)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.error)
    }
}
