package com.nakibul.hassan.habittracker.presentation.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nakibul.hassan.habittracker.data.local.HabitDatabase
import com.nakibul.hassan.habittracker.domain.repository.SettingsRepository
import com.nakibul.hassan.habittracker.shared.constant.SettingsKeys
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
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private lateinit var database: HabitDatabase

    private lateinit var viewModel: SettingsViewModel

    private val testDispatcher = StandardTestDispatcher()

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

    private fun setupDefaultSettings() {
        coEvery { settingsRepository.getSetting(SettingsKeys.DARK_MODE) } returns "false"
        coEvery { settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED) } returns "false"
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(settingsRepository, database)
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has default values`() = runTest {
        // Given
        setupDefaultSettings()

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.darkMode)
        assertTrue(state.notificationsEnabled)
        assertTrue(state.hapticFeedback)
        assertFalse(state.streakFreezeEnabled)
    }

    // ==================== Load Settings Tests ====================

    @Test
    fun `loadSettings loads dark mode setting`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(SettingsKeys.DARK_MODE) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED) } returns "false"

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.darkMode)
    }

    @Test
    fun `loadSettings loads notifications setting`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(SettingsKeys.DARK_MODE) } returns "false"
        coEvery { settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED) } returns "false"
        coEvery { settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED) } returns "false"

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.notificationsEnabled)
    }

    @Test
    fun `loadSettings loads haptic feedback setting`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(SettingsKeys.DARK_MODE) } returns "false"
        coEvery { settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK) } returns "false"
        coEvery { settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED) } returns "false"

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.hapticFeedback)
    }

    @Test
    fun `loadSettings loads streak freeze setting`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(SettingsKeys.DARK_MODE) } returns "false"
        coEvery { settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED) } returns "true"

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.streakFreezeEnabled)
    }

    @Test
    fun `loadSettings uses defaults when settings are null`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(any()) } returns null

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - check defaults
        val state = viewModel.uiState.value
        assertFalse(state.darkMode) // default false
        assertTrue(state.notificationsEnabled) // default true
        assertTrue(state.hapticFeedback) // default true
        assertFalse(state.streakFreezeEnabled) // default false
    }

    @Test
    fun `loadSettings handles exception gracefully`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(any()) } throws Exception("Database error")

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - should use defaults without crashing
        // No exception should be thrown
    }

    // ==================== Toggle Dark Mode Tests ====================

    @Test
    fun `toggleDarkMode saves setting to repository`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleDarkMode(true)
        advanceUntilIdle()

        // Then
        coVerify { settingsRepository.setSetting(SettingsKeys.DARK_MODE, "true") }
    }

    @Test
    fun `toggleDarkMode updates ui state`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleDarkMode(true)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.darkMode)
    }

    @Test
    fun `toggleDarkMode can toggle off`() = runTest {
        // Given
        coEvery { settingsRepository.getSetting(SettingsKeys.DARK_MODE) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK) } returns "true"
        coEvery { settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED) } returns "false"
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleDarkMode(false)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.darkMode)
        coVerify { settingsRepository.setSetting(SettingsKeys.DARK_MODE, "false") }
    }

    // ==================== Toggle Notifications Tests ====================

    @Test
    fun `toggleNotifications saves setting to repository`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleNotifications(false)
        advanceUntilIdle()

        // Then
        coVerify { settingsRepository.setSetting(SettingsKeys.NOTIFICATIONS_ENABLED, "false") }
    }

    @Test
    fun `toggleNotifications updates ui state`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleNotifications(false)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.notificationsEnabled)
    }

    // ==================== Toggle Haptic Feedback Tests ====================

    @Test
    fun `toggleHapticFeedback saves setting to repository`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleHapticFeedback(false)
        advanceUntilIdle()

        // Then
        coVerify { settingsRepository.setSetting(SettingsKeys.HAPTIC_FEEDBACK, "false") }
    }

    @Test
    fun `toggleHapticFeedback updates ui state`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleHapticFeedback(false)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.hapticFeedback)
    }

    // ==================== Toggle Streak Freeze Tests ====================

    @Test
    fun `toggleStreakFreeze saves setting to repository`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleStreakFreeze(true)
        advanceUntilIdle()

        // Then
        coVerify { settingsRepository.setSetting(SettingsKeys.STREAK_FREEZE_ENABLED, "true") }
    }

    @Test
    fun `toggleStreakFreeze updates ui state`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.toggleStreakFreeze(true)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.streakFreezeEnabled)
    }

    // ==================== Clear Message Tests ====================

    @Test
    fun `clearMessage sets message to null`() = runTest {
        // Given
        setupDefaultSettings()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.clearMessage()

        // Then
        assertNull(viewModel.uiState.value.message)
    }
}
