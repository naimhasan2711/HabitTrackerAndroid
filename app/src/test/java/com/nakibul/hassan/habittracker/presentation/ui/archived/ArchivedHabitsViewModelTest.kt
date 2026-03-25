package com.nakibul.hassan.habittracker.presentation.ui.archived

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.repository.HabitRepository
import com.nakibul.hassan.habittracker.domain.usecase.ArchiveHabitUseCase
import com.nakibul.hassan.habittracker.domain.usecase.DeleteHabitUseCase
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
class ArchivedHabitsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var habitRepository: HabitRepository

    @MockK
    private lateinit var archiveHabitUseCase: ArchiveHabitUseCase

    @MockK
    private lateinit var deleteHabitUseCase: DeleteHabitUseCase

    private lateinit var viewModel: ArchivedHabitsViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val archivedHabits = listOf(
        Habit(
            id = "habit-1",
            name = "Old Exercise",
            description = "Archived workout",
            icon = "fitness",
            color = 0xFF6200EE.toInt(),
            categoryId = "cat-1",
            reminderTime = null,
            frequency = "daily",
            archived = true,
            createdAt = "2024-01-01T10:00:00"
        ),
        Habit(
            id = "habit-2",
            name = "Old Reading",
            description = "Archived reading",
            icon = "book",
            color = 0xFF03DAC5.toInt(),
            categoryId = "cat-2",
            reminderTime = null,
            frequency = "daily",
            archived = true,
            createdAt = "2024-01-01T10:00:00"
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

    private fun createViewModel(): ArchivedHabitsViewModel {
        return ArchivedHabitsViewModel(
            habitRepository,
            archiveHabitUseCase,
            deleteHabitUseCase
        )
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has loading true`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)

        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has empty habits list`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)

        // When
        viewModel = createViewModel()

        // Then - before data loads
        assertTrue(viewModel.uiState.value.habits.isEmpty())
    }

    // ==================== Load Archived Habits Tests ====================

    @Test
    fun `loadArchivedHabits updates habits list`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.uiState.value.habits.size)
        assertEquals("Old Exercise", viewModel.uiState.value.habits[0].name)
    }

    @Test
    fun `loadArchivedHabits sets loading to false`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadArchivedHabits with empty list returns empty habits`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(emptyList())

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.habits.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadArchivedHabits handles error gracefully`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flow { throw Exception("Database error") }

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - should not crash, error is caught
        // Note: The ViewModel's catch block is empty, so state remains unchanged
    }

    // ==================== Restore Habit Tests ====================

    @Test
    fun `restoreHabit calls archive use case with false`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)
        coEvery { archiveHabitUseCase("habit-1", false) } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.restoreHabit("habit-1")
        advanceUntilIdle()

        // Then
        coVerify { archiveHabitUseCase("habit-1", false) }
    }

    @Test
    fun `restoreHabit can restore multiple habits`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)
        coEvery { archiveHabitUseCase(any(), any()) } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.restoreHabit("habit-1")
        viewModel.restoreHabit("habit-2")
        advanceUntilIdle()

        // Then
        coVerify { archiveHabitUseCase("habit-1", false) }
        coVerify { archiveHabitUseCase("habit-2", false) }
    }

    // ==================== Delete Habit Tests ====================

    @Test
    fun `deleteHabit calls delete use case`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)
        coEvery { deleteHabitUseCase("habit-1") } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.deleteHabit("habit-1")
        advanceUntilIdle()

        // Then
        coVerify { deleteHabitUseCase("habit-1") }
    }

    @Test
    fun `deleteHabit can delete multiple habits`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)
        coEvery { deleteHabitUseCase(any()) } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.deleteHabit("habit-1")
        viewModel.deleteHabit("habit-2")
        advanceUntilIdle()

        // Then
        coVerify { deleteHabitUseCase("habit-1") }
        coVerify { deleteHabitUseCase("habit-2") }
    }

    // ==================== All Archived Habits Tests ====================

    @Test
    fun `all loaded habits are archived`() = runTest {
        // Given
        every { habitRepository.getAllArchivedHabits() } returns flowOf(archivedHabits)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.habits.all { it.archived })
    }
}
