package com.nakibul.hassan.habittracker.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nakibul.hassan.habittracker.presentation.components.LoadingScreen
import com.nakibul.hassan.habittracker.shared.utils.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToHabitDetail: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    if (uiState.isLoading) {
        LoadingScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            // Screen title
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Month navigation
            MonthHeader(
                currentMonth = uiState.currentMonth,
                onPreviousMonth = viewModel::navigateToPreviousMonth,
                onNextMonth = viewModel::navigateToNextMonth
            )
            
            // Calendar grid
            CalendarGrid(
                currentMonth = uiState.currentMonth,
                selectedDate = uiState.selectedDate,
                completionByDate = uiState.completionByDate,
                onDateSelected = viewModel::selectDate
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Selected date info
            SelectedDateInfo(
                selectedDate = uiState.selectedDate,
                habits = uiState.habits,
                logs = uiState.logsForSelectedDate,
                onHabitClick = onNavigateToHabitDetail
            )
        }
    }
}

@Composable
private fun MonthHeader(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
        }
        
        Text(
            text = DateUtils.formatMonthYear(currentMonth),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: LocalDate,
    selectedDate: String,
    completionByDate: Map<String, Float>,
    onDateSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar days
        val startOfMonth = currentMonth.withDayOfMonth(1)
        val endOfMonth = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())
        val startDayOfWeek = startOfMonth.dayOfWeek.value // 1 = Monday
        
        val days = mutableListOf<LocalDate?>()
        
        // Add empty cells for days before the month starts
        repeat(startDayOfWeek - 1) { days.add(null) }
        
        // Add actual days
        var currentDay = startOfMonth
        while (!currentDay.isAfter(endOfMonth)) {
            days.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }
        
        // Fill remaining cells
        while (days.size % 7 != 0) {
            days.add(null)
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(((days.size / 7) * 48).dp),
            userScrollEnabled = false
        ) {
            items(days) { day ->
                if (day != null) {
                    val dateString = DateUtils.formatDate(day)
                    val isSelected = dateString == selectedDate
                    val isToday = day == LocalDate.now()
                    val completion = completionByDate[dateString] ?: 0f
                    
                    CalendarDay(
                        day = day.dayOfMonth,
                        isSelected = isSelected,
                        isToday = isToday,
                        completion = completion,
                        onClick = { onDateSelected(dateString) }
                    )
                } else {
                    Box(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    completion: Float,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        completion >= 1f -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
        completion > 0f -> MaterialTheme.colorScheme.secondary.copy(alpha = completion * 0.5f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        completion >= 0.5f -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = Modifier
            .size(44.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (isToday && !isSelected) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    )
                } else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun SelectedDateInfo(
    selectedDate: String,
    habits: List<com.nakibul.hassan.habittracker.domain.model.Habit>,
    logs: List<com.nakibul.hassan.habittracker.domain.model.HabitLog>,
    onHabitClick: (String) -> Unit
) {
    val completedHabitIds = logs.filter { it.completed }.map { it.habitId }.toSet()
    val completedCount = completedHabitIds.size
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = DateUtils.formatForDisplay(selectedDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "$completedCount/${habits.size} completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (habits.isEmpty()) {
            Text(
                text = "No habits for this day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(habits) { habit ->
                    val isCompleted = completedHabitIds.contains(habit.id)
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onHabitClick(habit.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCompleted) {
                                Color(habit.color).copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = habit.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle 
                                             else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isCompleted) Color(habit.color) 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

