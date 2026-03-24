package com.abdur.rahman.habittracker.presentation.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdur.rahman.habittracker.domain.model.Habit
import com.abdur.rahman.habittracker.domain.model.HabitLog
import com.abdur.rahman.habittracker.domain.usecase.GetActiveHabitsUseCase
import com.abdur.rahman.habittracker.domain.usecase.GetCalendarDataUseCase
import com.abdur.rahman.habittracker.domain.usecase.GetHabitLogsForDateUseCase
import com.abdur.rahman.habittracker.shared.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val selectedDate: String = DateUtils.getCurrentDate(),
    val habits: List<Habit> = emptyList(),
    val logsForMonth: List<HabitLog> = emptyList(),
    val logsForSelectedDate: List<HabitLog> = emptyList(),
    val completionByDate: Map<String, Float> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getActiveHabitsUseCase: GetActiveHabitsUseCase,
    private val getCalendarDataUseCase: GetCalendarDataUseCase,
    private val getHabitLogsForDateUseCase: GetHabitLogsForDateUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        val currentMonth = _uiState.value.currentMonth
        
        viewModelScope.launch {
            combine(
                getActiveHabitsUseCase(),
                getCalendarDataUseCase(currentMonth, 0)
            ) { habits, logs ->
                val completionByDate = logs.groupBy { it.date }
                    .mapValues { (_, dateLogs) ->
                        val completed = dateLogs.count { it.completed }
                        if (habits.isNotEmpty()) completed.toFloat() / habits.size else 0f
                    }
                
                Pair(habits, Pair(logs, completionByDate))
            }
                .catch { }
                .collect { (habits, logsData) ->
                    val (logs, completionByDate) = logsData
                    _uiState.update {
                        it.copy(
                            habits = habits,
                            logsForMonth = logs,
                            completionByDate = completionByDate,
                            isLoading = false
                        )
                    }
                    loadLogsForSelectedDate()
                }
        }
    }
    
    private fun loadLogsForSelectedDate() {
        viewModelScope.launch {
            getHabitLogsForDateUseCase(_uiState.value.selectedDate)
                .collect { logs ->
                    _uiState.update { it.copy(logsForSelectedDate = logs) }
                }
        }
    }
    
    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
        loadLogsForSelectedDate()
    }
    
    fun navigateToPreviousMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
        loadData()
    }
    
    fun navigateToNextMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }
        loadData()
    }
}
