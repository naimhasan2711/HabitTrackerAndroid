package com.nakibul.hassan.habittracker.presentation.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.model.HabitLog
import com.nakibul.hassan.habittracker.domain.usecase.ArchiveHabitUseCase
import com.nakibul.hassan.habittracker.domain.usecase.DeleteHabitUseCase
import com.nakibul.hassan.habittracker.domain.usecase.GetHabitByIdUseCase
import com.nakibul.hassan.habittracker.domain.usecase.GetHabitLogsInRangeUseCase
import com.nakibul.hassan.habittracker.domain.usecase.ToggleHabitCompletionUseCase
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
class HabitDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getHabitByIdUseCase: GetHabitByIdUseCase

    @MockK
    private lateinit var getHabitLogsInRangeUseCase: GetHabitLogsInRangeUseCase

    @MockK
    private lateinit var deleteHabitUseCase: DeleteHabitUseCase

    @MockK
    private lateinit var archiveHabitUseCase: ArchiveHabitUseCase

    @MockK
    private lateinit var toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: HabitDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testHabit = Habit(
        id = "habit-1",
        name = "Exercise",
        description = "Daily workout",
        icon = "fitness",
        color = 0xFF6200EE.toInt(),
        categoryId = "cat-1",
        reminderTime = "08:00",
        frequency = "daily",
        archived = false,
        createdAt = "2024-01-01T10:00:00"
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
            habitId = "habit-1",
            date = "2024-01-14",
            completed = true,
            completedAt = "2024-01-14T10:00:00"
        ),
        HabitLog(
            id = "log-3",
            habitId = "habit-1",
            date = "2024-01-13",
            completed = false,
            completedAt = null
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf("habitId" to "habit-1"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(): HabitDetailViewModel {
        return HabitDetailViewModel(
            savedStateHandle,
            getHabitByIdUseCase,
            getHabitLogsInRangeUseCase,
            deleteHabitUseCase,
            archiveHabitUseCase,
            toggleHabitCompletionUseCase
        )
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has loading true`() = runTest {
        // Given
        every { getHabitByIdUseCase(any()) } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has isDeleted false`() = runTest {
        // Given
        every { getHabitByIdUseCase(any()) } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()

        // Then
        assertFalse(viewModel.uiState.value.isDeleted)
    }

    // ==================== Load Details Tests ====================

    @Test
    fun `loadHabitDetails updates habit`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(testHabit, viewModel.uiState.value.habit)
        assertEquals("Exercise", viewModel.uiState.value.habit?.name)
    }

    @Test
    fun `loadHabitDetails updates recent logs`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(3, viewModel.uiState.value.recentLogs.size)
    }

    @Test
    fun `loadHabitDetails sorts logs by date descending`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val logs = viewModel.uiState.value.recentLogs
        assertTrue(logs[0].date >= logs[1].date)
        assertTrue(logs[1].date >= logs[2].date)
    }

    @Test
    fun `loadHabitDetails calculates completion rate`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - 2 completed out of 30 days = 2/30 ≈ 0.067
        assertEquals(2f / 30f, viewModel.uiState.value.completionRate, 0.001f)
    }

    @Test
    fun `loadHabitDetails sets loading to false`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadHabitDetails handles error`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flow { throw Exception("Not found") }
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals("Not found", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ==================== Delete Habit Tests ====================

    @Test
    fun `deleteHabit calls use case`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { deleteHabitUseCase("habit-1") } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.deleteHabit()
        advanceUntilIdle()

        // Then
        coVerify { deleteHabitUseCase("habit-1") }
    }

    @Test
    fun `deleteHabit sets isDeleted true on success`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { deleteHabitUseCase("habit-1") } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.deleteHabit()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isDeleted)
    }

    @Test
    fun `deleteHabit handles error`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { deleteHabitUseCase("habit-1") } throws Exception("Delete failed")
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.deleteHabit()
        advanceUntilIdle()

        // Then
        assertEquals("Delete failed", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isDeleted)
    }

    // ==================== Toggle Archive Tests ====================

    @Test
    fun `toggleArchive calls use case with inverted archive status`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { archiveHabitUseCase("habit-1", true) } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleArchive()
        advanceUntilIdle()

        // Then - habit was not archived, so should call with true
        coVerify { archiveHabitUseCase("habit-1", true) }
    }

    @Test
    fun `toggleArchive does nothing when habit is null`() = runTest {
        // Given - error loading habit
        every { getHabitByIdUseCase("habit-1") } returns flow { throw Exception("Not found") }
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleArchive()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { archiveHabitUseCase(any(), any()) }
    }

    @Test
    fun `toggleArchive handles error`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { archiveHabitUseCase(any(), any()) } throws Exception("Archive failed")
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleArchive()
        advanceUntilIdle()

        // Then
        assertEquals("Archive failed", viewModel.uiState.value.error)
    }

    // ==================== Toggle Habit Completion Tests ====================

    @Test
    fun `toggleHabitCompletion calls use case`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { toggleHabitCompletionUseCase("habit-1") } just Runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleHabitCompletion()
        advanceUntilIdle()

        // Then
        coVerify { toggleHabitCompletionUseCase("habit-1") }
    }

    @Test
    fun `toggleHabitCompletion handles error`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flowOf(testHabit)
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        coEvery { toggleHabitCompletionUseCase("habit-1") } throws Exception("Toggle failed")
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleHabitCompletion()
        advanceUntilIdle()

        // Then
        assertEquals("Toggle failed", viewModel.uiState.value.error)
    }

    // ==================== Clear Error Tests ====================

    @Test
    fun `clearError sets error to null`() = runTest {
        // Given
        every { getHabitByIdUseCase("habit-1") } returns flow { throw Exception("Error") }
        every { getHabitLogsInRangeUseCase(any(), any(), any()) } returns flowOf(testLogs)
        viewModel = createViewModel()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.error)
    }
}
