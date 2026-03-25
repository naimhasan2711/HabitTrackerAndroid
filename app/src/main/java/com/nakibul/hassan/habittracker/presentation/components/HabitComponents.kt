package com.nakibul.hassan.habittracker.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nakibul.hassan.habittracker.domain.model.Habit
import com.nakibul.hassan.habittracker.ui.theme.StreakFire

@Composable
fun HabitCard(
    habit: Habit,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    val habitColor = Color(habit.color)
    
    Card(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Habit icon with color background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(habitColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForName(habit.icon),
                    contentDescription = null,
                    tint = habitColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Habit Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Streak Badge inline
                    if (habit.currentStreak > 0) {
                        StreakBadge(streak = habit.currentStreak)
                    }
                }
                
                if (!habit.description.isNullOrEmpty()) {
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Enhanced completion button
            CompletionButton(
                isCompleted = habit.isCompletedToday,
                onToggle = onToggleComplete,
                color = habitColor
            )
        }
    }
}

@Composable
fun CompletionButton(
    isCompleted: Boolean,
    onToggle: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCompleted) color else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "bg_color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "content_color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 1f,
        animationSpec = keyframes {
            durationMillis = 300
            1f at 0
            1.15f at 100
            0.95f at 200
            1f at 300
        },
        label = "button_scale"
    )
    
    Surface(
        modifier = modifier
            .scale(scale)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = if (isCompleted) 0.dp else 2.dp,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (isCompleted) "Completed" else "Mark complete",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isCompleted) "Done" else "Do it",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

@Composable
fun AnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkbox_scale"
    )
    
    val bounceScale by animateFloatAsState(
        targetValue = if (checked) 1f else 1f,
        animationSpec = keyframes {
            durationMillis = 400
            1f at 0
            1.2f at 100
            0.9f at 200
            1.05f at 300
            1f at 400
        },
        label = "bounce_scale"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .scale(if (checked) bounceScale else 1f)
            .clip(CircleShape)
            .background(if (checked) color else Color.Transparent)
            .clickable { onCheckedChange() }
            .then(
                if (!checked) {
                    Modifier
                        .background(Color.Transparent)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier
                    .size(size * 0.6f)
                    .scale(scale)
            )
        }
    }
}

@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = StreakFire.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Streak",
            tint = StreakFire,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$streak",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = StreakFire
        )
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )
    
    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        
        // Draw track
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
        
        // Draw progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = onAction) {
                Text(text = actionLabel)
            }
        }
    }
}

