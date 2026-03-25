package com.nakibul.hassan.habittracker.presentation.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Calendar : NavRoutes("calendar")
    data object Analytics : NavRoutes("analytics")
    data object Settings : NavRoutes("settings")
    data object AddHabit : NavRoutes("add_habit")
    data object EditHabit : NavRoutes("edit_habit/{habitId}") {
        fun createRoute(habitId: String) = "edit_habit/$habitId"
    }
    data object HabitDetail : NavRoutes("habit_detail/{habitId}") {
        fun createRoute(habitId: String) = "habit_detail/$habitId"
    }
    data object ArchivedHabits : NavRoutes("archived_habits")
}

