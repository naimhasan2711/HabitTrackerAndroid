package com.nakibul.hassan.habittracker.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.habittracker.domain.usecase.GetActiveHabitsUseCase
import com.nakibul.hassan.habittracker.domain.usecase.ToggleHabitCompletionUseCase
import com.nakibul.hassan.habittracker.shared.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getActiveHabitsUseCase: GetActiveHabitsUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHabits()
    }
    
    private fun loadHabits() {
        viewModelScope.launch {
            _uiState.update { it.copy(greeting = DateUtils.getGreeting()) }
            
            getActiveHabitsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { habits ->
                    val completedCount = habits.count { it.isCompletedToday }
                    val totalCount = habits.size
                    val percentage = if (totalCount > 0) {
                        completedCount.toFloat() / totalCount
                    } else 0f
                    
                    _uiState.update {
                        it.copy(
                            habits = habits,
                            completedCount = completedCount,
                            totalCount = totalCount,
                            completionPercentage = percentage,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
    
    fun toggleHabitCompletion(habitId: String) {
        viewModelScope.launch {
            try {
                toggleHabitCompletionUseCase(habitId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadHabits()
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

