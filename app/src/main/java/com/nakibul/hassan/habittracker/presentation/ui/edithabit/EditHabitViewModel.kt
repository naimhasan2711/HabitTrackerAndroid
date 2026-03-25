package com.nakibul.hassan.habittracker.presentation.ui.edithabit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.usecase.GetHabitByIdUseCase
import com.nakibul.hassan.habittracker.domain.usecase.UpdateHabitUseCase
import com.nakibul.hassan.habittracker.notification.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditHabitUiState(
    val habit: Habit? = null,
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val color: Int = 0,
    val frequency: String = "daily",
    val customDays: List<Int> = emptyList(),
    val reminderTime: String? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val nameError: String? = null
)

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val notificationHelper: NotificationHelper
) : ViewModel() {
    
    companion object {
        private const val TAG = "EditHabitViewModel"
    }
    
    private val habitId: String = checkNotNull(savedStateHandle["habitId"])
    
    private val _uiState = MutableStateFlow(EditHabitUiState())
    val uiState: StateFlow<EditHabitUiState> = _uiState.asStateFlow()
    
    init {
        loadHabit()
    }
    
    private fun loadHabit() {
        viewModelScope.launch {
            try {
                val habit = getHabitByIdUseCase(habitId).filterNotNull().first()
                _uiState.update {
                    it.copy(
                        habit = habit,
                        name = habit.name,
                        description = habit.description ?: "",
                        icon = habit.icon,
                        color = habit.color,
                        frequency = habit.frequency,
                        customDays = habit.customDays ?: emptyList(),
                        reminderTime = habit.reminderTime,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateIcon(icon: String) {
        _uiState.update { it.copy(icon = icon) }
    }
    
    fun updateColor(color: Int) {
        _uiState.update { it.copy(color = color) }
    }
    
    fun updateFrequency(frequency: String) {
        _uiState.update { it.copy(frequency = frequency) }
    }
    
    fun updateCustomDays(days: List<Int>) {
        _uiState.update { it.copy(customDays = days) }
    }
    
    fun updateReminderTime(time: String?) {
        _uiState.update { it.copy(reminderTime = time) }
    }
    
    fun saveHabit() {
        val state = _uiState.value
        val originalHabit = state.habit ?: return
        
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Name is required") }
            return
        }
        
        if (state.frequency == "custom" && state.customDays.isEmpty()) {
            _uiState.update { it.copy(error = "Please select at least one day") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val updatedHabit = originalHabit.copy(
                    name = state.name.trim(),
                    description = state.description.trim().takeIf { it.isNotEmpty() },
                    icon = state.icon,
                    color = state.color,
                    frequency = state.frequency,
                    customDays = if (state.frequency == "custom") state.customDays else null,
                    reminderTime = state.reminderTime
                )
                
                updateHabitUseCase(updatedHabit)
                
                // Update reminder scheduling
                if (updatedHabit.reminderTime != null) {
                    Log.d(TAG, "Scheduling reminder for updated habit: ${updatedHabit.name} at ${updatedHabit.reminderTime}")
                    notificationHelper.scheduleHabitReminder(updatedHabit)
                } else {
                    Log.d(TAG, "Cancelling reminder for habit: ${updatedHabit.name} (no reminder time set)")
                    notificationHelper.cancelHabitReminder(updatedHabit.id)
                }
                
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

