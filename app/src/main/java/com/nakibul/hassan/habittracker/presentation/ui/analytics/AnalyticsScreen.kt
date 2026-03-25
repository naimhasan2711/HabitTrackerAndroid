package com.nakibul.hassan.habittracker.presentation.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nakibul.hassan.habittracker.domain.model.HabitCompletionRate
import com.nakibul.hassan.habittracker.presentation.components.CircularProgressIndicator
import com.nakibul.hassan.habittracker.presentation.components.LoadingScreen
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    if (uiState.isLoading && uiState.analyticsData == null) {
        LoadingScreen()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen title
            item {
                Text(
                    text = "Analytics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Period selector
            item {
                PeriodSelector(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = viewModel::selectPeriod
                )
            }
            
            uiState.analyticsData?.let { data ->
                    // Overview card
                    item {
                        OverviewCard(
                            overallRate = data.overallCompletionRate,
                            activeHabits = data.activeHabitsCount,
                            bestStreak = data.bestStreak
                        )
                    }
                    
                    // Chart
                    item {
                        ChartSection(
                            completions = data.dailyCompletions.map { it.completionPercentage }
                        )
                    }
                    
                    // Habit breakdown
                    item {
                        Text(
                            text = "Habit Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    items(data.habitCompletions) { habitRate ->
                        HabitCompletionCard(habitRate = habitRate)
                    }
                }
            }
        }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: Int,
    onPeriodSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(7, 14, 30, 90).forEach { period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { onPeriodSelected(period) },
                label = { Text("$period days") }
            )
        }
    }
}

@Composable
private fun OverviewCard(
    overallRate: Float,
    activeHabits: Int,
    bestStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion rate
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = overallRate,
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "${(overallRate * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Completion",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            // Active habits
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.FormatListBulleted,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$activeHabits",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Active Habits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            // Best streak
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$bestStreak",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Best Streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ChartSection(
    completions: List<Float>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Daily Completion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (completions.isNotEmpty()) {
                val modelProducer = remember { CartesianChartModelProducer() }
                
                LaunchedEffect(completions) {
                    modelProducer.runTransaction {
                        columnSeries {
                            series(completions.map { (it * 100).toInt() })
                        }
                    }
                }
                
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer()
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitCompletionCard(
    habitRate: HabitCompletionRate
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(8.dp, 40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(habitRate.habitColor))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitRate.habitName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${habitRate.completedDays}/${habitRate.totalDays} days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "${(habitRate.completionRate * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(habitRate.habitColor)
            )
        }
    }
}

