package com.abdur.rahman.habittracker.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.abdur.rahman.habittracker.presentation.ui.addhabit.AddHabitScreen
import com.abdur.rahman.habittracker.presentation.ui.analytics.AnalyticsScreen
import com.abdur.rahman.habittracker.presentation.ui.archived.ArchivedHabitsScreen
import com.abdur.rahman.habittracker.presentation.ui.calendar.CalendarScreen
import com.abdur.rahman.habittracker.presentation.ui.detail.HabitDetailScreen
import com.abdur.rahman.habittracker.presentation.ui.edithabit.EditHabitScreen
import com.abdur.rahman.habittracker.presentation.ui.home.HomeScreen
import com.abdur.rahman.habittracker.presentation.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { 100 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -100 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -100 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { 100 },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToAddHabit = {
                    navController.navigate(NavRoutes.AddHabit.route)
                },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(NavRoutes.HabitDetail.createRoute(habitId))
                }
            )
        }
        
        composable(NavRoutes.Calendar.route) {
            CalendarScreen(
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(NavRoutes.HabitDetail.createRoute(habitId))
                }
            )
        }
        
        composable(NavRoutes.Analytics.route) {
            AnalyticsScreen()
        }
        
        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                onNavigateToArchivedHabits = {
                    navController.navigate(NavRoutes.ArchivedHabits.route)
                }
            )
        }
        
        composable(
            route = NavRoutes.AddHabit.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                )
            }
        ) {
            AddHabitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = NavRoutes.EditHabit.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            EditHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = NavRoutes.HabitDetail.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(NavRoutes.EditHabit.createRoute(habitId))
                }
            )
        }
        
        composable(NavRoutes.ArchivedHabits.route) {
            ArchivedHabitsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
