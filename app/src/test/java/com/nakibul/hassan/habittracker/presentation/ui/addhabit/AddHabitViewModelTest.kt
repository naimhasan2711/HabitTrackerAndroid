package com.nakibul.hassan.habittracker.presentation.ui.addhabit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.usecase.CreateHabitUseCase
import com.nakibul.hassan.habittracker.notification.NotificationHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddHabitViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var createHabitUseCase: CreateHabitUseCase

    @MockK
    private lateinit var notificationHelper: NotificationHelper

    private lateinit var viewModel: AddHabitViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        viewModel = AddHabitViewModel(createHabitUseCase, notificationHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // ==================== Update Name Tests ====================

    @Test
    fun `updateName updates state name`() {
        // When
        viewModel.updateName("Exercise")

        // Then
        assertEquals("Exercise", viewModel.uiState.value.name)
    }

    @Test
    fun `updateName clears name error`() {
        // Given - first set an error by saving with empty name
        viewModel.saveHabit()
        assertNotNull(viewModel.uiState.value.nameError)

        // When
        viewModel.updateName("Exercise")

        // Then
        assertNull(viewModel.uiState.value.nameError)
    }

    // ==================== Update Description Tests ====================

    @Test
    fun `updateDescription updates state description`() {
        // When
        viewModel.updateDescription("Daily workout routine")

        // Then
        assertEquals("Daily workout routine", viewModel.uiState.value.description)
    }

    // ==================== Update Icon Tests ====================

    @Test
    fun `updateIcon updates state icon`() {
        // When
        viewModel.updateIcon("fitness")

        // Then
        assertEquals("fitness", viewModel.uiState.value.icon)
    }

    // ==================== Update Color Tests ====================

    @Test
    fun `updateColor updates state color`() {
        // When
        val newColor = 0xFF6200EE.toInt()
        viewModel.updateColor(newColor)

        // Then
        assertEquals(newColor, viewModel.uiState.value.color)
    }

    // ==================== Update Frequency Tests ====================

    @Test
    fun `updateFrequency updates state frequency`() {
        // When
        viewModel.updateFrequency("weekly")

        // Then
        assertEquals("weekly", viewModel.uiState.value.frequency)
    }

    @Test
    fun `updateFrequency to custom does not affect customDays`() {
        // Given
        viewModel.updateCustomDays(listOf(1, 2, 3))

        // When
        viewModel.updateFrequency("custom")

        // Then
        assertEquals("custom", viewModel.uiState.value.frequency)
        assertEquals(listOf(1, 2, 3), viewModel.uiState.value.customDays)
    }

    // ==================== Update Custom Days Tests ====================

    @Test
    fun `updateCustomDays updates state customDays`() {
        // When
        viewModel.updateCustomDays(listOf(1, 3, 5))

        // Then
        assertEquals(listOf(1, 3, 5), viewModel.uiState.value.customDays)
    }

    @Test
    fun `updateCustomDays with empty list clears customDays`() {
        // Given
        viewModel.updateCustomDays(listOf(1, 2, 3))

        // When
        viewModel.updateCustomDays(emptyList())

        // Then
        assertTrue(viewModel.uiState.value.customDays.isEmpty())
    }

    // ==================== Update Reminder Time Tests ====================

    @Test
    fun `updateReminderTime updates state reminderTime`() {
        // When
        viewModel.updateReminderTime("09:00")

        // Then
        assertEquals("09:00", viewModel.uiState.value.reminderTime)
    }

    @Test
    fun `updateReminderTime with null clears reminderTime`() {
        // Given
        viewModel.updateReminderTime("09:00")

        // When
        viewModel.updateReminderTime(null)

        // Then
        assertNull(viewModel.uiState.value.reminderTime)
    }

    // ==================== Save Habit Validation Tests ====================

    @Test
    fun `saveHabit with empty name sets nameError`() {
        // Given - name is empty by default

        // When
        viewModel.saveHabit()

        // Then
        assertEquals("Name is required", viewModel.uiState.value.nameError)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `saveHabit with blank name sets nameError`() {
        // Given
        viewModel.updateName("   ")

        // When
        viewModel.saveHabit()

        // Then
        assertEquals("Name is required", viewModel.uiState.value.nameError)
    }

    @Test
    fun `saveHabit with custom frequency and no days sets error`() {
        // Given
        viewModel.updateName("Test Habit")
        viewModel.updateFrequency("custom")
        viewModel.updateCustomDays(emptyList())

        // When
        viewModel.saveHabit()

        // Then
        assertEquals("Please select at least one day", viewModel.uiState.value.error)
    }

    // ==================== Save Habit Success Tests ====================

    @Test
    fun `saveHabit creates habit successfully`() = runTest {
        // Given
        viewModel.updateName("Exercise")
        viewModel.updateDescription("Daily workout")
        viewModel.updateIcon("fitness")
        viewModel.updateColor(0xFF6200EE.toInt())
        viewModel.updateFrequency("daily")
        
        coEvery { createHabitUseCase(any()) } just Runs

        // When
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSaved)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify { createHabitUseCase(any()) }
    }

    @Test
    fun `saveHabit trims name and description`() = runTest {
        // Given
        viewModel.updateName("  Exercise  ")
        viewModel.updateDescription("  Daily workout  ")
        
        val capturedHabit = slot<Habit>()
        coEvery { createHabitUseCase(capture(capturedHabit)) } just Runs

        // When
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then
        assertEquals("Exercise", capturedHabit.captured.name)
        assertEquals("Daily workout", capturedHabit.captured.description)
    }

//    @Test
//    fun `saveHabit with reminder schedules notification`() = runTest(testDispatcher) {
//        // Given
//        viewModel.updateName("Exercise")
//        viewModel.updateReminderTime("09:00")
//
//        coEvery { createHabitUseCase(any()) } just Runs
//        every { notificationHelper.scheduleHabitReminder(any()) } just Runs
//
//        // When
//        viewModel.saveHabit()
//        advanceUntilIdle()
//
//        // Then - verify the save completed successfully with reminder
//        assertTrue(viewModel.uiState.value.isSaved)
//        verify { notificationHelper.scheduleHabitReminder(any()) }
//    }

    @Test
    fun `saveHabit without reminder does not schedule notification`() = runTest {
        // Given
        viewModel.updateName("Exercise")
        viewModel.updateReminderTime(null)
        
        coEvery { createHabitUseCase(any()) } just Runs

        // When
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then
        verify(exactly = 0) { notificationHelper.scheduleHabitReminder(any()) }
    }

    @Test
    fun `saveHabit with custom frequency includes customDays`() = runTest {
        // Given
        viewModel.updateName("Exercise")
        viewModel.updateFrequency("custom")
        viewModel.updateCustomDays(listOf(1, 3, 5))
        
        val capturedHabit = slot<Habit>()
        coEvery { createHabitUseCase(capture(capturedHabit)) } just Runs

        // When
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then
        assertEquals(listOf(1, 3, 5), capturedHabit.captured.customDays)
    }

    @Test
    fun `saveHabit with daily frequency excludes customDays`() = runTest {
        // Given
        viewModel.updateName("Exercise")
        viewModel.updateFrequency("daily")
        viewModel.updateCustomDays(listOf(1, 3, 5)) // This should be ignored
        
        val capturedHabit = slot<Habit>()
        coEvery { createHabitUseCase(capture(capturedHabit)) } just Runs

        // When
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then
        assertNull(capturedHabit.captured.customDays)
    }

    // ==================== Save Habit Error Tests ====================

    @Test
    fun `saveHabit handles exception`() = runTest {
        // Given
        viewModel.updateName("Exercise")
        coEvery { createHabitUseCase(any()) } throws Exception("Database error")

        // When
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Database error", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    // ==================== Clear Error Tests ====================

    @Test
    fun `clearError clears error state`() {
        // Given
        viewModel.updateName("Exercise")
        viewModel.updateFrequency("custom")
        viewModel.saveHabit() // This will cause an error
        assertNotNull(viewModel.uiState.value.error)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.error)
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has correct defaults`() {
        val state = viewModel.uiState.value

        assertEquals("", state.name)
        assertEquals("", state.description)
        assertEquals("daily", state.frequency)
        assertTrue(state.customDays.isEmpty())
        assertNull(state.reminderTime)
        assertFalse(state.isLoading)
        assertFalse(state.isSaved)
        assertNull(state.error)
        assertNull(state.nameError)
    }
}
