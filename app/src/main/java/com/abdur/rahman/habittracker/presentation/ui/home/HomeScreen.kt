package com.abdur.rahman.habittracker.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abdur.rahman.habittracker.presentation.components.CircularProgressIndicator
import com.abdur.rahman.habittracker.presentation.components.EmptyState
import com.abdur.rahman.habittracker.presentation.components.HabitCard
import com.abdur.rahman.habittracker.presentation.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddHabit: () -> Unit,
    onNavigateToHabitDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddHabit,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add habit"
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.habits.isEmpty() -> {
                    LoadingScreen()
                }
                uiState.habits.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Outlined.EventNote,
                        title = "No habits yet",
                        description = "Start building positive habits by adding your first one!",
                        actionLabel = "Add Habit",
                        onAction = onNavigateToAddHabit
                    )
                }
                else -> {
                    HomeContent(
                        uiState = uiState,
                        onToggleHabit = viewModel::toggleHabitCompletion,
                        onHabitClick = onNavigateToHabitDetail
                    )
                }
            }
        }
    }
    
    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error and clear it
            viewModel.clearError()
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onToggleHabit: (String) -> Unit,
    onHabitClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with progress
        item {
            ProgressHeader(
                greeting = uiState.greeting,
                completedCount = uiState.completedCount,
                totalCount = uiState.totalCount,
                completionPercentage = uiState.completionPercentage
            )
        }
        
        // Habits list
        item {
            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        itemsIndexed(
            items = uiState.habits,
            key = { _, habit -> habit.id }
        ) { index, habit ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 50
                    )
                ) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 50
                    )
                )
            ) {
                HabitCard(
                    habit = habit,
                    onToggleComplete = { onToggleHabit(habit.id) },
                    onClick = { onHabitClick(habit.id) }
                )
            }
        }
        
        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ProgressHeader(
    greeting: String,
    completedCount: Int,
    totalCount: Int,
    completionPercentage: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "$completedCount of $totalCount completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = getMotivationalMessage(completionPercentage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    CircularProgressIndicator(
                        progress = completionPercentage,
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                    
                    Text(
                        text = "${(completionPercentage * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun getMotivationalMessage(percentage: Float): String {
    return when {
        percentage >= 1f -> "🎉 Perfect day! Amazing work!"
        percentage >= 0.8f -> "🔥 Almost there! Keep going!"
        percentage >= 0.5f -> "💪 Great progress! You're doing well!"
        percentage >= 0.25f -> "🌱 Good start! Keep building momentum!"
        percentage > 0f -> "✨ Every step counts! Don't give up!"
        else -> "🚀 Ready to start your day?"
    }
}
