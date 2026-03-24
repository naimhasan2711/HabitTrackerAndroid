package com.abdur.rahman.habittracker.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abdur.rahman.habittracker.domain.repository.SettingsRepository
import com.abdur.rahman.habittracker.presentation.components.BottomNavItem
import com.abdur.rahman.habittracker.presentation.components.HabitTrackerBottomNavBar
import com.abdur.rahman.habittracker.presentation.components.bottomNavItems
import com.abdur.rahman.habittracker.presentation.navigation.AppNavHost
import com.abdur.rahman.habittracker.shared.constant.SettingsKeys
import com.abdur.rahman.habittracker.ui.theme.HabitTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val darkModeFlow = settingsRepository.getSettingFlow(SettingsKeys.DARK_MODE)
                .map { it?.toBoolean() ?: false }
            val isDarkMode by darkModeFlow.collectAsState(initial = false)
            
            HabitTrackerTheme(
                darkTheme = isDarkMode,
                dynamicColor = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    // Determine if we should show the bottom nav
                    val shouldShowBottomNav by remember(currentRoute) {
                        derivedStateOf {
                            bottomNavItems.any { it.route == currentRoute }
                        }
                    }
                    
                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomNav) {
                                HabitTrackerBottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { item ->
                                        navController.navigate(item.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
