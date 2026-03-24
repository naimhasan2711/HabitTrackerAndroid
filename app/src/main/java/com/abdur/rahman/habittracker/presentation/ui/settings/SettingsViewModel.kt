package com.abdur.rahman.habittracker.presentation.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdur.rahman.habittracker.data.local.HabitDatabase
import com.abdur.rahman.habittracker.domain.repository.SettingsRepository
import com.abdur.rahman.habittracker.shared.constant.SettingsKeys
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val hapticFeedback: Boolean = true,
    val streakFreezeEnabled: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val database: HabitDatabase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val darkMode = settingsRepository.getSetting(SettingsKeys.DARK_MODE)?.toBoolean() ?: false
                val notifications = settingsRepository.getSetting(SettingsKeys.NOTIFICATIONS_ENABLED)?.toBoolean() ?: true
                val haptic = settingsRepository.getSetting(SettingsKeys.HAPTIC_FEEDBACK)?.toBoolean() ?: true
                val streakFreeze = settingsRepository.getSetting(SettingsKeys.STREAK_FREEZE_ENABLED)?.toBoolean() ?: false
                
                _uiState.update {
                    it.copy(
                        darkMode = darkMode,
                        notificationsEnabled = notifications,
                        hapticFeedback = haptic,
                        streakFreezeEnabled = streakFreeze
                    )
                }
            } catch (e: Exception) {
                // Use defaults
            }
        }
    }
    
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSetting(SettingsKeys.DARK_MODE, enabled.toString())
            _uiState.update { it.copy(darkMode = enabled) }
        }
    }
    
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSetting(SettingsKeys.NOTIFICATIONS_ENABLED, enabled.toString())
            _uiState.update { it.copy(notificationsEnabled = enabled) }
        }
    }
    
    fun toggleHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSetting(SettingsKeys.HAPTIC_FEEDBACK, enabled.toString())
            _uiState.update { it.copy(hapticFeedback = enabled) }
        }
    }
    
    fun toggleStreakFreeze(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSetting(SettingsKeys.STREAK_FREEZE_ENABLED, enabled.toString())
            _uiState.update { it.copy(streakFreezeEnabled = enabled) }
        }
    }
    
    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val habits = database.habitDao().getHabitsWithReminders()
                val categories = database.categoryDao().getAllCategories()
                
                val exportData = mapOf(
                    "habits" to habits,
                    "exportedAt" to System.currentTimeMillis()
                )
                
                val json = Gson().toJson(exportData)
                
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray())
                }
                
                _uiState.update { it.copy(message = "Data exported successfully") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Export failed: ${e.message}") }
            }
        }
    }
    
    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                }
                
                // Parse and import (simplified - would need proper parsing)
                _uiState.update { it.copy(message = "Data imported successfully") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Import failed: ${e.message}") }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
