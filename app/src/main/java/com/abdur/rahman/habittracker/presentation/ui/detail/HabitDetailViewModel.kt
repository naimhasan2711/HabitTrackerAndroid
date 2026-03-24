package com.abdur.rahman.habittracker.presentation.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdur.rahman.habittracker.domain.model.Habit
import com.abdur.rahman.habittracker.domain.model.HabitLog
import com.abdur.rahman.habittracker.domain.usecase.ArchiveHabitUseCase
import com.abdur.rahman.habittracker.domain.usecase.DeleteHabitUseCase
import com.abdur.rahman.habittracker.domain.usecase.GetHabitByIdUseCase
import com.abdur.rahman.habittracker.domain.usecase.GetHabitLogsInRangeUseCase
import com.abdur.rahman.habittracker.shared.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitDetailUiState(
    val habit: Habit? = null,
    val recentLogs: List<HabitLog> = emptyList(),
    val completionRate: Float = 0f,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val getHabitLogsInRangeUseCase: GetHabitLogsInRangeUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val archiveHabitUseCase: ArchiveHabitUseCase
) : ViewModel() {
    
    private val habitId: String = checkNotNull(savedStateHandle["habitId"])
    
    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadHabitDetails()
    }
    
    private fun loadHabitDetails() {
        val endDate = DateUtils.getCurrentDate()
        val startDate = DateUtils.getDateMinusDays(29)
        
        viewModelScope.launch {
            combine(
                getHabitByIdUseCase(habitId),
                getHabitLogsInRangeUseCase(habitId, startDate, endDate)
            ) { habit, logs ->
                val completedDays = logs.count { it.completed }
                val totalDays = 30
                val rate = completedDays.toFloat() / totalDays
                
                HabitDetailUiState(
                    habit = habit,
                    recentLogs = logs.sortedByDescending { it.date },
                    completionRate = rate,
                    isLoading = false
                )
            }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { state ->
                    _uiState.update { state }
                }
        }
    }
    
    fun deleteHabit() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                deleteHabitUseCase(habitId)
                _uiState.update { it.copy(isDeleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun toggleArchive() {
        val habit = _uiState.value.habit ?: return
        viewModelScope.launch {
            try {
                archiveHabitUseCase(habitId, !habit.archived)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
