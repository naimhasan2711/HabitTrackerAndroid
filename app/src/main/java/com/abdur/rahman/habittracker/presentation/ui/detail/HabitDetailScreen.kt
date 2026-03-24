package com.abdur.rahman.habittracker.presentation.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abdur.rahman.habittracker.domain.model.Habit
import com.abdur.rahman.habittracker.domain.model.HabitLog
import com.abdur.rahman.habittracker.presentation.components.LoadingScreen
import com.abdur.rahman.habittracker.presentation.components.getIconForName
import com.abdur.rahman.habittracker.ui.theme.StreakFire
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenuExpanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onNavigateBack()
        }
    }
    
    val habitColor = uiState.habit?.let { Color(it.color) } ?: MaterialTheme.colorScheme.primary
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading && uiState.habit == null) {
            LoadingScreen(modifier = Modifier.padding(paddingValues))
        } else {
            uiState.habit?.let { habit ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Colored Header Section
                    item {
                        HabitDetailHeader(
                            habit = habit,
                            onNavigateBack = onNavigateBack,
                            onNavigateToEdit = onNavigateToEdit,
                            onMenuClick = { showMenuExpanded = true },
                            showMenu = showMenuExpanded,
                            onDismissMenu = { showMenuExpanded = false },
                            onArchiveClick = {
                                showMenuExpanded = false
                                viewModel.toggleArchive()
                            },
                            onDeleteClick = {
                                showMenuExpanded = false
                                showDeleteDialog = true
                            }
                        )
                    }
                    
                    // Content Section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Completion Status Card
                            CompletionStatusCard(
                                isCompletedToday = habit.isCompletedToday,
                                habitColor = habitColor
                            )
                            
                            // Stats Grid (2x2)
                            StatsGrid(
                                currentStreak = habit.currentStreak,
                                bestStreak = habit.longestStreak,
                                completionRate = uiState.completionRate,
                                frequency = habit.frequency
                            )
                            
                            // Description Section
                            if (!habit.description.isNullOrEmpty()) {
                                DescriptionSection(description = habit.description)
                            }
                            
                            // Last 30 Days Grid
                            Last30DaysSection(
                                logs = uiState.recentLogs,
                                habitColor = habitColor
                            )
                            
                            // Daily Reminder
                            habit.reminderTime?.let { reminderTime ->
                                ReminderSection(reminderTime = reminderTime)
                            }
                            
                            // Created Date
                            CreatedDateSection(createdAt = habit.createdAt)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Habit?") },
            text = { Text("This will permanently delete this habit and all its history. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteHabit()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HabitDetailHeader(
    habit: Habit,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onArchiveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val habitColor = Color(habit.color)
    val darkerColor = habitColor.copy(alpha = 0.9f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(habitColor, darkerColor)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Row {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                    
                    Box {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Color.White
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = onDismissMenu
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (habit.archived) "Unarchive" else "Archive") },
                                leadingIcon = {
                                    Icon(
                                        if (habit.archived) Icons.Default.Unarchive else Icons.Default.Archive,
                                        contentDescription = null
                                    )
                                },
                                onClick = onArchiveClick
                            )
                            
                            HorizontalDivider()
                            
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = onDeleteClick
                            )
                        }
                    }
                }
            }
            
            // Habit Icon and Name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Habit Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForName(habit.icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Habit Name
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Category (using frequency as category placeholder)
                Text(
                    text = habit.frequency.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun CompletionStatusCard(
    isCompletedToday: Boolean,
    habitColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Checkmark icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isCompletedToday) habitColor
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompletedToday) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCompletedToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Column {
                Text(
                    text = if (isCompletedToday) "Completed Today" else "Not Completed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isCompletedToday) "Great job!" else "You can do it!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(
    currentStreak: Int,
    bestStreak: Int,
    completionRate: Float,
    frequency: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                iconTint = StreakFire,
                value = "$currentStreak",
                label = "Current Streak",
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                icon = Icons.Default.EmojiEvents,
                iconTint = Color(0xFFFFD700), // Gold
                value = "$bestStreak",
                label = "Best Streak",
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.Percent,
                iconTint = MaterialTheme.colorScheme.primary,
                value = "${(completionRate * 100).toInt()}%",
                label = "Completion Rate",
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                icon = Icons.Default.CalendarMonth,
                iconTint = Color(0xFF00897B), // Teal
                value = frequency.replaceFirstChar { it.uppercase() },
                label = "Frequency",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Column {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Last30DaysSection(
    logs: List<HabitLog>,
    habitColor: Color
) {
    val completedDates = remember(logs) {
        logs.filter { it.completed }.map { it.date }.toSet()
    }
    
    // Generate last 30 days
    val last30Days = remember {
        val today = LocalDate.now()
        (0 until 30).map { daysAgo ->
            today.minusDays(daysAgo.toLong())
        }.reversed()
    }
    
    Column {
        Text(
            text = "Last 30 Days",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Grid of 30 days (10 columns x 3 rows)
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            userScrollEnabled = false
        ) {
            items(last30Days) { date ->
                val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val isCompleted = completedDates.contains(dateStr)
                val isToday = date == LocalDate.now()
                
                DayIndicator(
                    isCompleted = isCompleted,
                    isToday = isToday,
                    habitColor = habitColor
                )
            }
        }
    }
}

@Composable
private fun DayIndicator(
    isCompleted: Boolean,
    isToday: Boolean,
    habitColor: Color
) {
    val backgroundColor = when {
        isCompleted -> habitColor
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Empty - just the colored box
    }
}

@Composable
private fun ReminderSection(reminderTime: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Column {
                Text(
                    text = "Daily Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatReminderTime(reminderTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CreatedDateSection(createdAt: String) {
    val formattedDate = remember(createdAt) {
        try {
            val date = LocalDate.parse(createdAt)
            date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        } catch (e: Exception) {
            createdAt
        }
    }
    
    Text(
        text = "Created $formattedDate",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

private fun formatReminderTime(time: String): String {
    return try {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        String.format("%d:%02d %s", displayHour, minute, amPm)
    } catch (e: Exception) {
        time
    }
}
