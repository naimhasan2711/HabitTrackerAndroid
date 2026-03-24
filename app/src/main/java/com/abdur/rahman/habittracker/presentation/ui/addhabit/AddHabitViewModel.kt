package com.abdur.rahman.habittracker.presentation.ui.addhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdur.rahman.habittracker.domain.model.Habit
import com.abdur.rahman.habittracker.domain.usecase.CreateHabitUseCase
import com.abdur.rahman.habittracker.shared.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()
    
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
        
        // Validate
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
                val habit = Habit(
                    id = UUID.randomUUID().toString(),
                    name = state.name.trim(),
                    description = state.description.trim().takeIf { it.isNotEmpty() },
                    icon = state.icon,
                    color = state.color,
                    frequency = state.frequency,
                    customDays = if (state.frequency == "custom") state.customDays else null,
                    reminderTime = state.reminderTime,
                    createdAt = DateUtils.getCurrentDateTime()
                )
                
                createHabitUseCase(habit)
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
