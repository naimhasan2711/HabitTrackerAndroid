package com.nakibul.hassan.habittracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.nakibul.hassan.habittracker.presentation.navigation.NavRoutes

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        route = NavRoutes.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    data object Calendar : BottomNavItem(
        route = NavRoutes.Calendar.route,
        title = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )
    
    data object Analytics : BottomNavItem(
        route = NavRoutes.Analytics.route,
        title = "Analytics",
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics
    )
    
    data object Settings : BottomNavItem(
        route = NavRoutes.Settings.route,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Calendar,
    BottomNavItem.Analytics,
    BottomNavItem.Settings
)

@Composable
fun HabitTrackerBottomNavBar(
    currentRoute: String?,
    onNavigate: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, 
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

