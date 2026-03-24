package com.abdur.rahman.habittracker.presentation.ui.home

import com.abdur.rahman.habittracker.domain.model.Habit

data class HomeUiState(
    val greeting: String = "",
    val habits: List<Habit> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val completionPercentage: Float = 0f,
    val isLoading: Boolean = true,
    val error: String? = null
)
