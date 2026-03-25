package com.nakibul.hassan.habittracker.presentation.ui.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.domain.repository.HabitRepository
import com.nakibul.hassan.habittracker.domain.usecase.ArchiveHabitUseCase
import com.nakibul.hassan.habittracker.domain.usecase.DeleteHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArchivedHabitsUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ArchivedHabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val archiveHabitUseCase: ArchiveHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ArchivedHabitsUiState())
    val uiState: StateFlow<ArchivedHabitsUiState> = _uiState.asStateFlow()
    
    init {
        loadArchivedHabits()
    }
    
    private fun loadArchivedHabits() {
        viewModelScope.launch {
            habitRepository.getAllArchivedHabits()
                .catch { }
                .collect { habits ->
                    _uiState.update { it.copy(habits = habits, isLoading = false) }
                }
        }
    }
    
    fun restoreHabit(habitId: String) {
        viewModelScope.launch {
            archiveHabitUseCase(habitId, false)
        }
    }
    
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            deleteHabitUseCase(habitId)
        }
    }
}

