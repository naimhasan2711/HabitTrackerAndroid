package com.nakibul.hassan.habittracker.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nakibul.hassan.habittracker.presentation.ui.addhabit.AddHabitScreen
import com.nakibul.hassan.habittracker.presentation.ui.analytics.AnalyticsScreen
import com.nakibul.hassan.habittracker.presentation.ui.archived.ArchivedHabitsScreen
import com.nakibul.hassan.habittracker.presentation.ui.calendar.CalendarScreen
import com.nakibul.hassan.habittracker.presentation.ui.detail.HabitDetailScreen
import com.nakibul.hassan.habittracker.presentation.ui.edithabit.EditHabitScreen
import com.nakibul.hassan.habittracker.presentation.ui.home.HomeScreen
import com.nakibul.hassan.habittracker.presentation.ui.settings.SettingsScreen

// Premium transition durations
private const val TRANSITION_DURATION = 400
private const val FADE_DURATION = 300

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
            fadeIn(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) + slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) + slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
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
        
        // Add Habit - Premium slide up animation
        composable(
            route = NavRoutes.AddHabit.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) {
            AddHabitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Edit Habit - Premium slide transition
        composable(
            route = NavRoutes.EditHabit.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            EditHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Habit Detail - Premium scale and fade transition
        composable(
            route = NavRoutes.HabitDetail.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleIn(
                    initialScale = 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth / 3 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    targetScale = 0.85f,
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth / 3 },
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            }
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
        
        // Archived Habits - Slide from bottom transition
        composable(
            route = NavRoutes.ArchivedHabits.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = FADE_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ) + slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight / 2 },
                    animationSpec = tween(
                        durationMillis = TRANSITION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) {
            ArchivedHabitsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

