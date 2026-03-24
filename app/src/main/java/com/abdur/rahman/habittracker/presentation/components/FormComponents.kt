package com.abdur.rahman.habittracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.abdur.rahman.habittracker.shared.constant.HabitColors
import com.abdur.rahman.habittracker.shared.constant.HabitIcons

@Composable
fun IconPicker(
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconMap = mapOf(
        "fitness_center" to Icons.Default.FitnessCenter,
        "directions_run" to Icons.Default.DirectionsRun,
        "self_improvement" to Icons.Default.SelfImprovement,
        "local_drink" to Icons.Default.LocalDrink,
        "restaurant" to Icons.Default.Restaurant,
        "book" to Icons.Default.Book,
        "school" to Icons.Default.School,
        "code" to Icons.Default.Code,
        "work" to Icons.Default.Work,
        "attach_money" to Icons.Default.AttachMoney,
        "savings" to Icons.Default.Savings,
        "favorite" to Icons.Default.Favorite,
        "psychology" to Icons.Default.Psychology,
        "bedtime" to Icons.Default.Bedtime,
        "alarm" to Icons.Default.Alarm,
        "brush" to Icons.Default.Brush,
        "music_note" to Icons.Default.MusicNote,
        "sports_esports" to Icons.Default.SportsEsports,
        "pets" to Icons.Default.Pets,
        "eco" to Icons.Default.Eco,
        "smoking_rooms" to Icons.Default.SmokingRooms,
        "no_drinks" to Icons.Default.NoDrinks,
        "sports" to Icons.Default.Sports,
        "hiking" to Icons.Default.Hiking
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier.height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(HabitIcons.icons) { iconName ->
            val icon = iconMap[iconName] ?: Icons.Default.Category
            val isSelected = iconName == selectedIcon
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onIconSelected(iconName) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconName,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ColorPicker(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(HabitColors.colors) { color ->
            val isSelected = color == selectedColor
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .then(
                        if (isSelected) Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        ) else Modifier
                    )
                    .clickable { onColorSelected(color) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FrequencySelector(
    selectedFrequency: String,
    onFrequencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("daily", "weekly", "custom").forEach { frequency ->
            val isSelected = frequency == selectedFrequency
            val label = frequency.replaceFirstChar { it.uppercase() }
            
            FilterChip(
                selected = isSelected,
                onClick = { onFrequencySelected(frequency) },
                label = { Text(label) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null
            )
        }
    }
}

@Composable
fun DaySelector(
    selectedDays: List<Int>,
    onDaysChanged: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf("M" to 1, "T" to 2, "W" to 3, "T" to 4, "F" to 5, "S" to 6, "S" to 7)
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { (label, dayNum) ->
            val isSelected = selectedDays.contains(dayNum)
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        val newDays = if (isSelected) {
                            selectedDays - dayNum
                        } else {
                            selectedDays + dayNum
                        }
                        onDaysChanged(newDays.sorted())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun getIconForName(iconName: String): ImageVector {
    return when (iconName) {
        "fitness_center" -> Icons.Default.FitnessCenter
        "directions_run" -> Icons.Default.DirectionsRun
        "self_improvement" -> Icons.Default.SelfImprovement
        "local_drink" -> Icons.Default.LocalDrink
        "restaurant" -> Icons.Default.Restaurant
        "book" -> Icons.Default.Book
        "school" -> Icons.Default.School
        "code" -> Icons.Default.Code
        "work" -> Icons.Default.Work
        "attach_money" -> Icons.Default.AttachMoney
        "savings" -> Icons.Default.Savings
        "favorite" -> Icons.Default.Favorite
        "psychology" -> Icons.Default.Psychology
        "bedtime" -> Icons.Default.Bedtime
        "alarm" -> Icons.Default.Alarm
        "brush" -> Icons.Default.Brush
        "music_note" -> Icons.Default.MusicNote
        "sports_esports" -> Icons.Default.SportsEsports
        "pets" -> Icons.Default.Pets
        "eco" -> Icons.Default.Eco
        "smoking_rooms" -> Icons.Default.SmokingRooms
        "no_drinks" -> Icons.Default.NoDrinks
        "sports" -> Icons.Default.Sports
        "hiking" -> Icons.Default.Hiking
        else -> Icons.Default.Category
    }
}
