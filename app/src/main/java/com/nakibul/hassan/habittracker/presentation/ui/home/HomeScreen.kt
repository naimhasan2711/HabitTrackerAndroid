package com.nakibul.hassan.habittracker.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nakibul.hassan.habittracker.presentation.components.CircularProgressIndicator
import com.nakibul.hassan.habittracker.presentation.components.EmptyState
import com.nakibul.hassan.habittracker.presentation.components.HabitCard
import com.nakibul.hassan.habittracker.presentation.components.LoadingScreen
import com.nakibul.hassan.habittracker.ui.theme.GradientEnd
import com.nakibul.hassan.habittracker.ui.theme.GradientMiddle
import com.nakibul.hassan.habittracker.ui.theme.GradientStart

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
            PremiumFloatingActionButton(
                onClick = onNavigateToAddHabit
            )
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
private fun PremiumFloatingActionButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_rotation"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_elevation"
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(18.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(18.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add habit",
            modifier = Modifier
                .size(28.dp)
                .rotate(rotation)
        )
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
            PremiumProgressHeader(
                greeting = uiState.greeting,
                completedCount = uiState.completedCount,
                totalCount = uiState.totalCount,
                completionPercentage = uiState.completionPercentage
            )
        }
        
        // Habits list
        item {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Habits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${uiState.habits.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        itemsIndexed(
            items = uiState.habits,
            key = { _, habit -> habit.id }
        ) { index, habit ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 60,
                        easing = FastOutSlowInEasing
                    )
                ) + slideInVertically(
                    initialOffsetY = { 80 },
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 60,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 60,
                        easing = FastOutSlowInEasing
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
private fun PremiumProgressHeader(
    greeting: String,
    completedCount: Int,
    totalCount: Int,
    completionPercentage: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")
    
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_shift"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = GradientStart.copy(alpha = 0.15f),
                spotColor = GradientEnd.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.9f + gradientShift * 0.1f),
                            GradientMiddle.copy(alpha = 0.85f),
                            GradientEnd.copy(alpha = 0.9f - gradientShift * 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$completedCount of $totalCount completed",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = getMotivationalMessage(completionPercentage),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                // Premium circular progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .padding(6.dp)
                ) {
                    CircularProgressIndicator(
                        progress = completionPercentage,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 8.dp,
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${(completionPercentage * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Keep legacy function for compatibility
@Composable
private fun ProgressHeader(
    greeting: String,
    completedCount: Int,
    totalCount: Int,
    completionPercentage: Float
) {
    PremiumProgressHeader(greeting, completedCount, totalCount, completionPercentage)
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

