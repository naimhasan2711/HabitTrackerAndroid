package com.abdur.rahman.habittracker.presentation.ui.addhabit

import com.abdur.rahman.habittracker.shared.constant.HabitColors
import com.abdur.rahman.habittracker.shared.constant.HabitIcons

data class AddHabitUiState(
    val name: String = "",
    val description: String = "",
    val icon: String = HabitIcons.icons.first(),
    val color: Int = HabitColors.colors.first(),
    val frequency: String = "daily",
    val customDays: List<Int> = emptyList(),
    val reminderTime: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val nameError: String? = null
)
